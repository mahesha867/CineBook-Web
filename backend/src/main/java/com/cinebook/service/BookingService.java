package com.cinebook.service;

import com.cinebook.dao.*;
import com.cinebook.domain.model.*;
import com.cinebook.domain.valueobject.PriceBreakdown;
import com.cinebook.exception.BookingNotFoundException;
import com.cinebook.exception.DuplicateCancellationException;
import com.cinebook.exception.SeatHeldException;
import com.cinebook.exception.SeatUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core Transactional Business Logic for CineBook Reservations.
 * 
 * DBMS showcase:
 * - Isolation Level: REPEATABLE READ.
 * - Atomic Multi-table Transaction: bookings + booking_seats + booking_snacks.
 * - Deterministic Row-Level Locking with SELECT ... FOR UPDATE sorted by seat_id
 *   to eliminate deadlock conditions during high concurrency.
 * - Invokes or syncs with MySQL Stored Procedures (sp_cancel_booking) and Triggers.
 */
@Service
public class BookingService {

    private static final Logger log = LoggerFactory.getLogger(BookingService.class);

    private final BookingDao bookingDao;
    private final SeatDao seatDao;
    private final ShowDao showDao;
    private final ScreenDao screenDao;
    private final SnackDao snackDao;
    private final UserDao userDao;
    private final SeatHoldDao seatHoldDao;
    private final PricingEngine pricingEngine;
    private final WaitlistService waitlistService;
    private final QrCodeService qrCodeService;

    @Value("${cinebook.booking.cancellation-refund-rate:0.80}")
    private BigDecimal cancellationRefundRate;

    @Autowired
    public BookingService(
            BookingDao bookingDao,
            SeatDao seatDao,
            ShowDao showDao,
            ScreenDao screenDao,
            SnackDao snackDao,
            UserDao userDao,
            SeatHoldDao seatHoldDao,
            PricingEngine pricingEngine,
            WaitlistService waitlistService,
            QrCodeService qrCodeService
    ) {
        this.bookingDao = bookingDao;
        this.seatDao = seatDao;
        this.showDao = showDao;
        this.screenDao = screenDao;
        this.snackDao = snackDao;
        this.userDao = userDao;
        this.seatHoldDao = seatHoldDao;
        this.pricingEngine = pricingEngine;
        this.waitlistService = waitlistService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Confirms a cinema reservation atomically.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public Booking createBooking(
            int showId,
            List<Integer> seatIds,
            Map<String, Integer> snackOrders, // snackId -> quantity
            String couponCode,
            String customerName,
            String customerEmail,
            String customerPhone,
            String sessionId
    ) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new IllegalArgumentException("At least one seat must be selected.");
        }

        Show show = showDao.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        Screen screen = screenDao.findById(show.getScreenId())
                .orElseThrow(() -> new IllegalStateException("Screen not found for show: " + showId));

        // 1. Deterministic Row-Level Locking (eliminates deadlocks in high concurrency)
        List<Integer> sortedSeatIds = new ArrayList<>(seatIds);
        Collections.sort(sortedSeatIds);
        bookingDao.lockSeatsForUpdate(showId, sortedSeatIds);

        // 2. Concurrency Safety: Check if any seat is already confirmed booked
        Set<Integer> bookedSeats = seatDao.findBookedSeatIdsForShow(showId);
        for (Integer sid : sortedSeatIds) {
            if (bookedSeats.contains(sid)) {
                throw new SeatUnavailableException("Seat #" + sid + " is already booked by another customer.");
            }
        }

        // 3. Verify Active Seat Holds
        Map<Integer, SeatHold> activeHolds = seatHoldDao.findActiveHoldsMapForShow(showId);
        for (Integer sid : sortedSeatIds) {
            if (activeHolds.containsKey(sid)) {
                SeatHold hold = activeHolds.get(sid);
                if (sessionId != null && !sessionId.equals(hold.getSessionId())) {
                    throw new SeatHeldException("Seat #" + sid + " is currently held by another user.");
                }
            }
        }

        // 4. Load Seat Entities & Snack Entities
        List<Seat> seats = seatDao.findByIds(sortedSeatIds);
        if (seats.size() != sortedSeatIds.size()) {
            throw new IllegalArgumentException("One or more invalid seat IDs provided.");
        }

        Map<Snack, Integer> snackMap = new HashMap<>();
        if (snackOrders != null && !snackOrders.isEmpty()) {
            for (Map.Entry<String, Integer> entry : snackOrders.entrySet()) {
                String snackId = entry.getKey();
                int qty = entry.getValue() != null ? entry.getValue() : 0;
                if (qty > 0) {
                    Snack snack = snackDao.findById(snackId)
                            .orElseThrow(() -> new IllegalArgumentException("Snack not found: " + snackId));
                    snackMap.put(snack, qty);
                }
            }
        }

        // Calculate occupancy percentage for dynamic pricing
        double occupancy = screen.getTotalCapacity() > 0
                ? ((double) bookedSeats.size() / screen.getTotalCapacity()) * 100.0
                : 0.0;

        // 5. Compute Pricing Breakdown
        PriceBreakdown breakdown = pricingEngine.calculatePriceBreakdown(
                seats,
                snackMap,
                show.getShowTime(),
                occupancy,
                show.getBasePriceMultiplier(),
                couponCode
        );

        // 6. Generate Collision-Free Booking ID (e.g. CB1001)
        long seq = bookingDao.getNextBookingSequence();
        String bookingId = "CB" + seq;

        // 7. Get or Create Customer Record
        User user = userDao.getOrCreateCustomer(customerName, customerEmail, customerPhone);

        // 8. Assemble Aggregate Root: Booking
        Booking booking = new Booking();
        booking.setBookingId(bookingId);
        booking.setUserId(user.getUserId());
        booking.setShowId(showId);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTicketSubtotal(breakdown.getTicketSubtotal());
        booking.setTicketDiscount(breakdown.getCouponDiscount());
        booking.setSnackSubtotal(breakdown.getSnackTotal());
        booking.setFinalTotal(breakdown.getGrandTotal());
        booking.setCouponCode(couponCode != null && !couponCode.trim().isEmpty() ? couponCode.trim().toUpperCase() : null);
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setRefundAmount(BigDecimal.ZERO);

        // 9. Assemble Booking Seats
        List<BookingSeat> bookingSeats = new ArrayList<>();
        BigDecimal perSeatAdjusted = seats.isEmpty() ? BigDecimal.ZERO
                : breakdown.getTicketSubtotal().divide(BigDecimal.valueOf(seats.size()), 2, java.math.RoundingMode.HALF_UP);

        for (Seat seat : seats) {
            BookingSeat bs = new BookingSeat(bookingId, showId, seat.getSeatId(), perSeatAdjusted, seat.getTier().name());
            bookingSeats.add(bs);
        }

        // 10. Assemble Booking Snacks with Historical Unit Price
        List<BookingSnack> bookingSnacks = new ArrayList<>();
        for (Map.Entry<Snack, Integer> entry : snackMap.entrySet()) {
            Snack snack = entry.getKey();
            int qty = entry.getValue();
            BigDecimal sub = snack.getPrice().multiply(BigDecimal.valueOf(qty));
            BookingSnack bsn = new BookingSnack(bookingId, snack.getSnackId(), qty, snack.getPrice(), sub);
            bookingSnacks.add(bsn);
        }

        // 11. Transactional Multi-Table Persistence
        try {
            bookingDao.insertBooking(booking);
            bookingDao.insertBookingSeats(bookingId, bookingSeats);
            if (!bookingSnacks.isEmpty()) {
                bookingDao.insertBookingSnacks(bookingId, bookingSnacks);
            }
        } catch (org.springframework.dao.DuplicateKeyException e) {
            throw new SeatUnavailableException("Seat already booked by a concurrent customer. Transaction rolled back.");
        }

        // 12. Free the Temporary Hold Records for this Session
        if (sessionId != null) {
            seatHoldDao.releaseHolds(showId, sortedSeatIds, sessionId);
        }

        // Populate return entity
        booking.setSeats(bookingSeats);
        booking.setSnacks(bookingSnacks);
        booking.setMovieTitle(show.getMovieTitle());
        booking.setShowTime(show.getShowTime());
        booking.setScreenName(screen.getScreenName());
        booking.setCustomerName(user.getFullName());
        booking.setCustomerEmail(user.getEmail());
        booking.setCustomerPhone(user.getPhone());

        return booking;
    }

    /**
     * Transactionally cancels an existing booking, issues an 80% refund,
     * frees the reserved seats, and auto-promotes the first waitlisted customer.
     */
    @Transactional(isolation = Isolation.REPEATABLE_READ, rollbackFor = Exception.class)
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingDao.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new DuplicateCancellationException("Booking " + bookingId + " has already been cancelled.");
        }

        // 1. Calculate 80% refund
        BigDecimal refund = booking.calculateRefundAmount(cancellationRefundRate);

        // 2. Update status and record refund amount
        LocalDateTime now = LocalDateTime.now();
        bookingDao.updateBookingStatus(bookingId, BookingStatus.CANCELLED, refund, now);

        // 3. Free seats so other customers can book
        bookingDao.deleteBookingSeats(bookingId);

        // 4. Auto-Promote Waitlist: Check if any customer is queued for this show
        waitlistService.promoteNextWaitlistEntry(booking.getShowId());

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setRefundAmount(refund);
        booking.setCancelledAt(now);
        booking.getSeats().clear(); // seats freed

        return booking;
    }

    public Optional<Booking> getBookingById(String bookingId) {
        return bookingDao.findById(bookingId);
    }

    public List<Booking> getBookingsByPhoneOrEmail(String identifier) {
        if (identifier == null || identifier.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String clean = identifier.trim();
        Optional<User> userOpt = clean.contains("@")
                ? userDao.findByEmail(clean)
                : userDao.findByPhone(clean);

        return userOpt.map(user -> bookingDao.findByUserId(user.getUserId())).orElse(Collections.emptyList());
    }

    public String generateTicketQrCode(Booking booking) {
        String payload = String.format("CINEBOOK|%s|SHOW:%d|USER:%s|TOTAL:Rs.%.2f",
                booking.getBookingId(),
                booking.getShowId(),
                booking.getCustomerPhone() != null ? booking.getCustomerPhone() : "GUEST",
                booking.getFinalTotal()
        );
        return qrCodeService.generateQrCodeBase64(payload, 250, 250);
    }
}
