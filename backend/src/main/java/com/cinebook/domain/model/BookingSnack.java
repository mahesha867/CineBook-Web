package com.cinebook.domain.model;

import java.math.BigDecimal;

/**
 * OOP Entity: BookingSnack.
 * 
 * Stores historical concession items attached to a booking.
 * Academic Rule: Preserves historical unit price and subtotal at time of booking,
 * ensuring menu price updates do not alter historical audit records.
 */
public class BookingSnack {

    private Integer bookingSnackId;
    private String bookingId;
    private String snackId;
    private int quantity;
    private BigDecimal historicalUnitPrice;
    private BigDecimal subtotal;

    // Joined fields
    private String snackName;

    public BookingSnack() {
    }

    public BookingSnack(String bookingId, String snackId, int quantity, BigDecimal historicalUnitPrice, BigDecimal subtotal) {
        this.bookingId = bookingId;
        this.snackId = snackId;
        this.quantity = quantity;
        this.historicalUnitPrice = historicalUnitPrice;
        this.subtotal = subtotal;
    }

    public Integer getBookingSnackId() {
        return bookingSnackId;
    }

    public void setBookingSnackId(Integer bookingSnackId) {
        this.bookingSnackId = bookingSnackId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getSnackId() {
        return snackId;
    }

    public void setSnackId(String snackId) {
        this.snackId = snackId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getHistoricalUnitPrice() {
        return historicalUnitPrice;
    }

    public void setHistoricalUnitPrice(BigDecimal historicalUnitPrice) {
        this.historicalUnitPrice = historicalUnitPrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public String getSnackName() {
        return snackName;
    }

    public void setSnackName(String snackName) {
        this.snackName = snackName;
    }
}

