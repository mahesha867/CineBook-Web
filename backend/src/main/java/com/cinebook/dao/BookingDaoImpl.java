package com.cinebook.dao;

import com.cinebook.domain.model.Booking;
import com.cinebook.domain.model.BookingSeat;
import com.cinebook.domain.model.BookingSnack;
import com.cinebook.domain.model.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;

@Repository
public class BookingDaoImpl implements BookingDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public BookingDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    private static final RowMapper<Booking> BOOKING_ROW_MAPPER = (rs, rowNum) -> {
        Booking b = new Booking();
        b.setBookingId(rs.getString("booking_id"));
        b.setUserId(rs.getInt("user_id"));
        b.setShowId(rs.getInt("show_id"));
        b.setBookingTime(rs.getTimestamp("booking_time").toLocalDateTime());
        b.setTicketSubtotal(rs.getBigDecimal("ticket_subtotal"));
        b.setTicketDiscount(rs.getBigDecimal("ticket_discount"));
        b.setSnackSubtotal(rs.getBigDecimal("snack_subtotal"));
        b.setFinalTotal(rs.getBigDecimal("final_total"));
        b.setCouponCode(rs.getString("coupon_code"));
        b.setStatus(BookingStatus.valueOf(rs.getString("status").toUpperCase()));
        b.setRefundAmount(rs.getBigDecimal("refund_amount"));
        Timestamp cancelledTs = rs.getTimestamp("cancelled_at");
        if (cancelledTs != null) {
            b.setCancelledAt(cancelledTs.toLocalDateTime());
        }

        // Additional joined fields
        b.setMovieTitle(rs.getString("movie_title"));
        b.setShowTime(rs.getTimestamp("show_time").toLocalDateTime());
        b.setScreenName(rs.getString("screen_name"));
        b.setCustomerName(rs.getString("customer_name"));
        b.setCustomerEmail(rs.getString("customer_email"));
        b.setCustomerPhone(rs.getString("customer_phone"));
        return b;
    };

    @Override
    public synchronized long getNextBookingSequence() {
        jdbcTemplate.update("UPDATE booking_seq SET next_val = next_val + 1 WHERE id = 1");
        Long next = jdbcTemplate.queryForObject("SELECT next_val FROM booking_seq WHERE id = 1", Long.class);
        return (next != null) ? next : 1001L;
    }

    @Override
    public void lockSeatsForUpdate(int showId, List<Integer> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) return;

        // Deterministic sorting to prevent deadlocks in high concurrency
        List<Integer> sortedSeatIds = new ArrayList<>(seatIds);
        Collections.sort(sortedSeatIds);

        String sql = "SELECT seat_id FROM seats WHERE seat_id IN (:ids) ORDER BY seat_id ASC FOR UPDATE";
        MapSqlParameterSource params = new MapSqlParameterSource("ids", sortedSeatIds);
        namedParameterJdbcTemplate.queryForList(sql, params, Integer.class);
    }

    @Override
    public void insertBooking(Booking booking) {
        String sql = """
            INSERT INTO bookings (
                booking_id, user_id, show_id, booking_time,
                ticket_subtotal, ticket_discount, snack_subtotal,
                final_total, coupon_code, status, refund_amount, cancelled_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        jdbcTemplate.update(sql,
                booking.getBookingId(),
                booking.getUserId(),
                booking.getShowId(),
                Timestamp.valueOf(booking.getBookingTime() != null ? booking.getBookingTime() : LocalDateTime.now()),
                booking.getTicketSubtotal(),
                booking.getTicketDiscount(),
                booking.getSnackSubtotal(),
                booking.getFinalTotal(),
                booking.getCouponCode(),
                booking.getStatus().name(),
                booking.getRefundAmount() != null ? booking.getRefundAmount() : BigDecimal.ZERO,
                booking.getCancelledAt() != null ? Timestamp.valueOf(booking.getCancelledAt()) : null
        );
    }

    @Override
    public void insertBookingSeats(String bookingId, List<BookingSeat> seats) {
        if (seats == null || seats.isEmpty()) return;
        String sql = "INSERT INTO booking_seats (booking_id, show_id, seat_id, unit_price, seat_tier) VALUES (?, ?, ?, ?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (BookingSeat bs : seats) {
            batchArgs.add(new Object[]{bookingId, bs.getShowId(), bs.getSeatId(), bs.getUnitPrice(), bs.getSeatTier()});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public void insertBookingSnacks(String bookingId, List<BookingSnack> snacks) {
        if (snacks == null || snacks.isEmpty()) return;
        String sql = """
            INSERT INTO booking_snacks (booking_id, snack_id, quantity, historical_unit_price, subtotal)
            VALUES (?, ?, ?, ?, ?)
        """;
        List<Object[]> batchArgs = new ArrayList<>();
        for (BookingSnack sn : snacks) {
            batchArgs.add(new Object[]{bookingId, sn.getSnackId(), sn.getQuantity(), sn.getHistoricalUnitPrice(), sn.getSubtotal()});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public Optional<Booking> findById(String bookingId) {
        String sql = """
            SELECT b.*, m.title AS movie_title, s.show_time, sc.screen_name,
                   u.full_name AS customer_name, u.email AS customer_email, u.phone AS customer_phone
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            JOIN users u ON b.user_id = u.user_id
            WHERE b.booking_id = ?
        """;
        try {
            Booking booking = jdbcTemplate.queryForObject(sql, BOOKING_ROW_MAPPER, bookingId);
            if (booking != null) {
                booking.setSeats(findSeatsByBookingId(bookingId));
                booking.setSnacks(findSnacksByBookingId(bookingId));
            }
            return Optional.ofNullable(booking);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Booking> findByUserId(int userId) {
        String sql = """
            SELECT b.*, m.title AS movie_title, s.show_time, sc.screen_name,
                   u.full_name AS customer_name, u.email AS customer_email, u.phone AS customer_phone
            FROM bookings b
            JOIN shows s ON b.show_id = s.show_id
            JOIN movies m ON s.movie_id = m.movie_id
            JOIN screens sc ON s.screen_id = sc.screen_id
            JOIN users u ON b.user_id = u.user_id
            WHERE b.user_id = ?
            ORDER BY b.booking_time DESC
        """;
        List<Booking> list = jdbcTemplate.query(sql, BOOKING_ROW_MAPPER, userId);
        for (Booking b : list) {
            b.setSeats(findSeatsByBookingId(b.getBookingId()));
            b.setSnacks(findSnacksByBookingId(b.getBookingId()));
        }
        return list;
    }

    @Override
    public List<BookingSeat> findSeatsByBookingId(String bookingId) {
        String sql = """
            SELECT bs.*, s.row_char, s.seat_num
            FROM booking_seats bs
            JOIN seats s ON bs.seat_id = s.seat_id
            WHERE bs.booking_id = ?
            ORDER BY s.row_char ASC, s.seat_num ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BookingSeat bs = new BookingSeat();
            bs.setBookingSeatId(rs.getInt("booking_seat_id"));
            bs.setBookingId(rs.getString("booking_id"));
            bs.setShowId(rs.getInt("show_id"));
            bs.setSeatId(rs.getInt("seat_id"));
            bs.setUnitPrice(rs.getBigDecimal("unit_price"));
            bs.setSeatTier(rs.getString("seat_tier"));
            bs.setRowChar(rs.getString("row_char").charAt(0));
            bs.setSeatNum(rs.getInt("seat_num"));
            return bs;
        }, bookingId);
    }

    @Override
    public List<BookingSnack> findSnacksByBookingId(String bookingId) {
        String sql = """
            SELECT bs.*, sn.name AS snack_name
            FROM booking_snacks bs
            JOIN snacks sn ON bs.snack_id = sn.snack_id
            WHERE bs.booking_id = ?
            ORDER BY bs.booking_snack_id ASC
        """;
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            BookingSnack bsn = new BookingSnack();
            bsn.setBookingSnackId(rs.getInt("booking_snack_id"));
            bsn.setBookingId(rs.getString("booking_id"));
            bsn.setSnackId(rs.getString("snack_id"));
            bsn.setQuantity(rs.getInt("quantity"));
            bsn.setHistoricalUnitPrice(rs.getBigDecimal("historical_unit_price"));
            bsn.setSubtotal(rs.getBigDecimal("subtotal"));
            bsn.setSnackName(rs.getString("snack_name"));
            return bsn;
        }, bookingId);
    }

    @Override
    public void updateBookingStatus(String bookingId, BookingStatus status, BigDecimal refundAmount, LocalDateTime cancelledAt) {
        String sql = "UPDATE bookings SET status = ?, refund_amount = ?, cancelled_at = ? WHERE booking_id = ?";
        jdbcTemplate.update(sql,
                status.name(),
                refundAmount != null ? refundAmount : BigDecimal.ZERO,
                cancelledAt != null ? Timestamp.valueOf(cancelledAt) : null,
                bookingId
        );
    }

    @Override
    public void deleteBookingSeats(String bookingId) {
        jdbcTemplate.update("DELETE FROM booking_seats WHERE booking_id = ?", bookingId);
    }

    @Override
    public CancelResult callStoredProcedureCancel(String bookingId) {
        return jdbcTemplate.execute((java.sql.Connection con) -> {
            try (CallableStatement cs = con.prepareCall("{CALL sp_cancel_booking(?, ?, ?)}")) {
                cs.setString(1, bookingId);
                cs.registerOutParameter(2, Types.DECIMAL);
                cs.registerOutParameter(3, Types.VARCHAR);
                cs.execute();
                BigDecimal refund = cs.getBigDecimal(2);
                String code = cs.getString(3);
                return new CancelResult(code, refund != null ? refund : BigDecimal.ZERO);
            }
        });
    }
}
