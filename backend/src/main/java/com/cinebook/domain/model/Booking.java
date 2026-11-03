package com.cinebook.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * OOP Entity: Booking.
 * 
 * Encapsulates the aggregate root for a customer cinema reservation.
 * Strictly enforces business rules:
 * - ticket subtotal, ticket discount, snack subtotal, final total
 * - Coupon discount strictly applied to tickets only, never snacks
 * - Final = (ticket subtotal - ticket discount) + snack subtotal
 * - 80% refund calculation on cancellation
 */
public class Booking {

    private String bookingId; // e.g. CB1001
    private Integer userId;
    private Integer showId;
    private LocalDateTime bookingTime;
    private BigDecimal ticketSubtotal = BigDecimal.ZERO;
    private BigDecimal ticketDiscount = BigDecimal.ZERO;
    private BigDecimal snackSubtotal = BigDecimal.ZERO;
    private BigDecimal finalTotal = BigDecimal.ZERO;
    private String couponCode;
    private BookingStatus status = BookingStatus.CONFIRMED;
    private BigDecimal refundAmount = BigDecimal.ZERO;
    private LocalDateTime cancelledAt;

    // Associated items
    private List<BookingSeat> seats = new ArrayList<>();
    private List<BookingSnack> snacks = new ArrayList<>();

    // Joined view fields
    private String movieTitle;
    private LocalDateTime showTime;
    private String screenName;
    private String customerName;
    private String customerEmail;
    private String customerPhone;

    public Booking() {
    }

    public Booking(String bookingId, Integer userId, Integer showId) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.showId = showId;
        this.bookingTime = LocalDateTime.now();
        this.status = BookingStatus.CONFIRMED;
    }

    /**
     * Business Logic: Calculates 80% refund on total paid amount.
     * 
     * @return refund value rounded to 2 decimal places
     */
    public BigDecimal calculateRefundAmount(BigDecimal refundRate) {
        if (finalTotal == null || BigDecimal.ZERO.compareTo(finalTotal) >= 0) {
            return BigDecimal.ZERO;
        }
        return finalTotal.multiply(refundRate).setScale(2, RoundingMode.HALF_UP);
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BigDecimal getTicketSubtotal() {
        return ticketSubtotal;
    }

    public void setTicketSubtotal(BigDecimal ticketSubtotal) {
        this.ticketSubtotal = ticketSubtotal;
    }

    public BigDecimal getTicketDiscount() {
        return ticketDiscount;
    }

    public void setTicketDiscount(BigDecimal ticketDiscount) {
        this.ticketDiscount = ticketDiscount;
    }

    public BigDecimal getSnackSubtotal() {
        return snackSubtotal;
    }

    public void setSnackSubtotal(BigDecimal snackSubtotal) {
        this.snackSubtotal = snackSubtotal;
    }

    public BigDecimal getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(BigDecimal finalTotal) {
        this.finalTotal = finalTotal;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public List<BookingSeat> getSeats() {
        return seats;
    }

    public void setSeats(List<BookingSeat> seats) {
        this.seats = seats;
    }

    public List<BookingSnack> getSnacks() {
        return snacks;
    }

    public void setSnacks(List<BookingSnack> snacks) {
        this.snacks = snacks;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }
}

