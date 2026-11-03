package com.cinebook.dao;

import com.cinebook.domain.model.Booking;
import com.cinebook.domain.model.BookingSeat;
import com.cinebook.domain.model.BookingSnack;
import com.cinebook.domain.model.BookingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingDao {
    long getNextBookingSequence();
    void lockSeatsForUpdate(int showId, List<Integer> seatIds);
    void insertBooking(Booking booking);
    void insertBookingSeats(String bookingId, List<BookingSeat> seats);
    void insertBookingSnacks(String bookingId, List<BookingSnack> snacks);
    Optional<Booking> findById(String bookingId);
    List<Booking> findByUserId(int userId);
    List<BookingSeat> findSeatsByBookingId(String bookingId);
    List<BookingSnack> findSnacksByBookingId(String bookingId);
    void updateBookingStatus(String bookingId, BookingStatus status, BigDecimal refundAmount, LocalDateTime cancelledAt);
    void deleteBookingSeats(String bookingId);
    
    /**
     * DBMS Showcase: Directly executes stored procedure sp_cancel_booking.
     */
    CancelResult callStoredProcedureCancel(String bookingId);

    record CancelResult(String resultCode, BigDecimal refundAmount) {}
}

