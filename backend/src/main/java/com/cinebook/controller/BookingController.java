package com.cinebook.controller;

import com.cinebook.domain.model.Booking;
import com.cinebook.dto.request.CreateBookingRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.BookingDetailResponse;
import com.cinebook.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingDetailResponse>> createBooking(
            @Valid @RequestBody CreateBookingRequest req
    ) {
        Booking booking = bookingService.createBooking(
                req.getShowId(),
                req.getSeatIds(),
                req.getSnackOrders(),
                req.getCouponCode(),
                req.getCustomerName(),
                req.getCustomerEmail(),
                req.getCustomerPhone(),
                req.getSessionId()
        );

        String qr = bookingService.generateTicketQrCode(booking);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking confirmed successfully", new BookingDetailResponse(booking, qr)));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<ApiResponse<BookingDetailResponse>> getBookingById(@PathVariable String bookingId) {
        return bookingService.getBookingById(bookingId)
                .map(b -> {
                    String qr = bookingService.generateTicketQrCode(b);
                    return ResponseEntity.ok(ApiResponse.ok(new BookingDetailResponse(b, qr)));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<ApiResponse<Booking>> cancelBooking(@PathVariable String bookingId) {
        Booking cancelled = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled. 80% refund issued.", cancelled));
    }

    @GetMapping("/lookup")
    public ResponseEntity<ApiResponse<List<Booking>>> lookupBookings(@RequestParam String query) {
        List<Booking> bookings = bookingService.getBookingsByPhoneOrEmail(query);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }
}

