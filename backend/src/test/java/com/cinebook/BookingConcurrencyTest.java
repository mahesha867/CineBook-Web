package com.cinebook;

import com.cinebook.domain.model.Booking;
import com.cinebook.exception.SeatUnavailableException;
import com.cinebook.service.BookingService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Advanced DBMS Concurrency Showcase Test.
 * 
 * Verifies that in REPEATABLE READ isolation with deterministic row-level locking
 * (SELECT ... FOR UPDATE ordered by seat_id), two simultaneous threads trying to book
 * the exact same seat cannot cause a race condition or double booking.
 * Exactly 1 thread must succeed, and exactly 1 must fail.
 */
@SpringBootTest
class BookingConcurrencyTest {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @DisplayName("DBMS Concurrency: 2 simultaneous threads booking the exact same seat -> Exactly 1 winner, 1 failure")
    void testConcurrentBookingSameSeatAllowsExactlyOneWinner() throws Exception {
        int targetShowId = 1;
        // Use a high seat number that hasn't been booked, e.g. seat_id 50 (Row E, Seat 10 on Screen 1)
        int targetSeatId = 50;

        // Clean any existing booking or hold on this seat
        jdbcTemplate.update("DELETE FROM booking_seats WHERE seat_id = ?", targetSeatId);
        jdbcTemplate.update("DELETE FROM seat_holds WHERE seat_id = ? AND show_id = ?", targetSeatId, targetShowId);

        int numberOfThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch endGate = new CountDownLatch(numberOfThreads);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);
        List<String> confirmedBookingIds = Collections.synchronizedList(new ArrayList<>());

        for (int i = 1; i <= numberOfThreads; i++) {
            final int threadNum = i;
            executor.submit(() -> {
                try {
                    startGate.await(); // Synchronize thread start to trigger simultaneous collision
                    Booking b = bookingService.createBooking(
                            targetShowId,
                            List.of(targetSeatId),
                            Map.of(),
                            null,
                            "Concurrent User " + threadNum,
                            "user" + threadNum + "_" + System.currentTimeMillis() + "@test.com",
                            "980000000" + threadNum,
                            "sess_concurrent_" + threadNum
                    );
                    successCount.incrementAndGet();
                    confirmedBookingIds.add(b.getBookingId());
                } catch (SeatUnavailableException e) {
                    failureCount.incrementAndGet();
                } catch (Exception e) {
                    // Any other failure (e.g. lock contention) also counts as non-winner
                    failureCount.incrementAndGet();
                } finally {
                    endGate.countDown();
                }
            });
        }

        // Fire both threads simultaneously
        startGate.countDown();
        boolean completed = endGate.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(completed).isTrue();
        // Crucial ACID verification: Exactly 1 winner, 1 failure
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failureCount.get()).isEqualTo(1);

        // Database Verification: Check booking_seats table contains exactly 1 row for targetSeatId in this show
        Integer seatRowCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM booking_seats bs JOIN bookings b ON bs.booking_id = b.booking_id WHERE b.show_id = ? AND bs.seat_id = ?",
                Integer.class,
                targetShowId,
                targetSeatId
        );
        assertThat(seatRowCount).isEqualTo(1);

        // Cleanup test booking
        if (!confirmedBookingIds.isEmpty()) {
            for (String bid : confirmedBookingIds) {
                jdbcTemplate.update("DELETE FROM booking_seats WHERE booking_id = ?", bid);
                jdbcTemplate.update("DELETE FROM bookings WHERE booking_id = ?", bid);
            }
        }
    }
}

