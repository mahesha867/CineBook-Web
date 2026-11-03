package com.cinebook;

import com.cinebook.dao.SeatHoldDao;
import com.cinebook.dao.SnackDao;
import com.cinebook.dao.WaitlistDao;
import com.cinebook.domain.model.Booking;
import com.cinebook.domain.model.BookingStatus;
import com.cinebook.domain.model.WaitlistEntry;
import com.cinebook.domain.model.WaitlistStatus;
import com.cinebook.exception.DuplicateCancellationException;
import com.cinebook.service.BookingService;
import com.cinebook.service.SeatHoldService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class BookingServiceIntegrationTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SeatHoldService seatHoldService;

    @Autowired
    private SeatHoldDao seatHoldDao;

    @Autowired
    private SnackDao snackDao;

    @Autowired
    private WaitlistDao waitlistDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("Academic Requirement 1: 80% refund issued and seats freed on cancellation")
    void testCancellationIssues80PercentRefundAndFreesSeats() {
        int showId = 2;
        int seatId = 15; // Row B, Seat 5 on Screen 1

        jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id = ?", seatId);
        jdbcTemplate.update("DELETE FROM seat_holds WHERE seat_id = ?", seatId);

        // 1. Create confirmed booking
        Booking booking = bookingService.createBooking(
                showId,
                List.of(seatId),
                Map.of("S001", 1), // 1 Popcorn (Rs. 120)
                null,
                "Refund Test User",
                "refund_" + System.currentTimeMillis() + "@test.com",
                "9811" + (System.currentTimeMillis() % 1000000),
                "sess_refund_test"
        );

        BigDecimal totalPaid = booking.getFinalTotal();
        assertThat(totalPaid).isGreaterThan(BigDecimal.ZERO);

        // 2. Cancel booking
        Booking cancelled = bookingService.cancelBooking(booking.getBookingId());

        // 3. Verify status and 80% refund
        BigDecimal expectedRefund = totalPaid.multiply(new BigDecimal("0.80")).setScale(2, RoundingMode.HALF_UP);
        assertThat(cancelled.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(cancelled.getRefundAmount()).isEqualByComparingTo(expectedRefund);

        // 4. Verify seats were freed in database
        Integer seatCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM booking_seats WHERE booking_id = ?",
                Integer.class,
                booking.getBookingId()
        );
        assertThat(seatCount).isEqualTo(0);

        // 5. Verify snack records remain for historical audit
        Integer snackCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM booking_snacks WHERE booking_id = ?",
                Integer.class,
                booking.getBookingId()
        );
        assertThat(snackCount).isEqualTo(1);
    }

    @Test
    @DisplayName("Academic Requirement 2: Duplicate cancellation attempt is strictly rejected")
    void testDuplicateCancellationRejected() {
        int showId = 2;
        int seatId = 16;

        jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id = ?", seatId);

        Booking booking = bookingService.createBooking(
                showId,
                List.of(seatId),
                Map.of(),
                null,
                "Duplicate Cancel User",
                "dup_cancel_" + System.currentTimeMillis() + "@test.com",
                "9812" + (System.currentTimeMillis() % 1000000),
                "sess_dup_cancel"
        );

        // First cancellation -> succeeds
        bookingService.cancelBooking(booking.getBookingId());

        // Second cancellation attempt -> DuplicateCancellationException
        assertThatThrownBy(() -> bookingService.cancelBooking(booking.getBookingId()))
                .isInstanceOf(DuplicateCancellationException.class)
                .hasMessageContaining("already been cancelled");
    }

    @Test
    @DisplayName("Academic Requirement 3: Historical snack price stays unchanged after menu price update")
    void testHistoricalSnackPricePreservedAfterMenuUpdate() {
        int showId = 2;
        int seatId = 17;

        jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id = ?", seatId);

        // Order S001 (Popcorn, currently 120.00)
        Booking booking = bookingService.createBooking(
                showId,
                List.of(seatId),
                Map.of("S001", 2),
                null,
                "Snack Audit User",
                "snack_audit_" + System.currentTimeMillis() + "@test.com",
                "9813" + (System.currentTimeMillis() % 1000000),
                "sess_snack_audit"
        );

        // Alter the menu price in snacks table to Rs. 200.00
        BigDecimal originalPrice = new BigDecimal("120.00");
        BigDecimal inflatedPrice = new BigDecimal("200.00");
        snackDao.updatePrice("S001", inflatedPrice);

        try {
            // Verify historical record inside booking_snacks table
            BigDecimal historicalUnitPrice = jdbcTemplate.queryForObject(
                    "SELECT historical_unit_price FROM booking_snacks WHERE booking_id = ? AND snack_id = 'S001'",
                    BigDecimal.class,
                    booking.getBookingId()
            );

            // Academic Rule: Historical price in past order must remain Rs. 120.00
            assertThat(historicalUnitPrice).isEqualByComparingTo(originalPrice);
        } finally {
            // Reset snack menu price
            snackDao.updatePrice("S001", originalPrice);
        }
    }

    @Test
    @DisplayName("Academic Requirement 4: Waitlist customer is auto-promoted when a booking is cancelled")
    void testWaitlistAutoPromotionOnCancellation() {
        int showId = 3;
        int seatId = 18;

        jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id = ?", seatId);

        // 1. Create a confirmed booking
        Booking booking = bookingService.createBooking(
                showId,
                List.of(seatId),
                Map.of(),
                null,
                "Show Attendee",
                "attendee_" + System.currentTimeMillis() + "@test.com",
                "9814" + (System.currentTimeMillis() % 1000000),
                "sess_attendee"
        );

        // 2. Put customer on waitlist for this show
        WaitlistEntry waitlistEntry = new WaitlistEntry(showId, booking.getUserId(), 1, "REGULAR");
        int waitlistId = waitlistDao.insertWaitlist(waitlistEntry);

        try {
            // 3. Cancel the booking
            bookingService.cancelBooking(booking.getBookingId());

            // 4. Verify waitlist entry was automatically promoted
            Optional<WaitlistEntry> updatedOpt = waitlistDao.findByShowId(showId).stream()
                    .filter(w -> w.getWaitlistId() == waitlistId)
                    .findFirst();

            assertThat(updatedOpt).isPresent();
            assertThat(updatedOpt.get().getStatus()).isEqualTo(WaitlistStatus.PROMOTED);
            assertThat(updatedOpt.get().getPromotedAt()).isNotNull();
        } finally {
            jdbcTemplate.update("DELETE FROM waitlist WHERE waitlist_id = ?", waitlistId);
        }
    }

    @Test
    @DisplayName("Academic Requirement 5: Seat hold expiry releases seats back to available")
    void testSeatHoldExpiryReleasesSeats() {
        int showId = 3;
        int seatId = 19;

        // Clean previous
        jdbcTemplate.update("DELETE FROM seat_holds WHERE seat_id = ? AND show_id = ?", seatId, showId);

        // Insert hold already in the past (expired 1 minute ago)
        jdbcTemplate.update(
                "INSERT INTO seat_holds (show_id, seat_id, session_id, held_at, expires_at) VALUES (?, ?, 'sess_expired', NOW() - INTERVAL 10 MINUTE, NOW() - INTERVAL 1 MINUTE)",
                showId, seatId
        );

        // Trigger cleanup
        seatHoldService.cleanupExpiredHolds();

        // Verify hold record was purged
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM seat_holds WHERE seat_id = ? AND show_id = ?",
                Integer.class,
                seatId, showId
        );
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("Academic Requirement 6: Atomic transaction rollback on mid-booking failure leaves zero orphaned records")
    void testAtomicRollbackOnFailure() {
        int showId = 3;
        int invalidSeatId = 99999; // Non-existent seat ID to force failure during seat validation / foreign key check

        int initialBookings = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookings", Integer.class);
        int initialSeats = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM booking_seats", Integer.class);

        assertThatThrownBy(() -> bookingService.createBooking(
                showId,
                List.of(invalidSeatId),
                Map.of("S001", 1),
                null,
                "Rollback User",
                "rollback@test.com",
                "9900000000",
                "sess_rollback"
        )).isInstanceOf(Exception.class);

        int postBookings = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM bookings", Integer.class);
        int postSeats = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM booking_seats", Integer.class);

        // Assert strictly zero orphaned records were persisted
        assertThat(postBookings).isEqualTo(initialBookings);
        assertThat(postSeats).isEqualTo(initialSeats);
    }
}
