package com.cinebook.domain.model;

import java.math.BigDecimal;

/**
 * OOP Entity: BookingSeat.
 * 
 * Captures the reserved seat along with historical unit price and tier.
 */
public class BookingSeat {

    private Integer bookingSeatId;
    private String bookingId;
    private Integer showId;
    private Integer seatId;
    private BigDecimal unitPrice;
    private String seatTier;

    // Joined fields
    private char rowChar;
    private int seatNum;

    public BookingSeat() {
    }

    public BookingSeat(String bookingId, Integer showId, Integer seatId, BigDecimal unitPrice, String seatTier) {
        this.bookingId = bookingId;
        this.showId = showId;
        this.seatId = seatId;
        this.unitPrice = unitPrice;
        this.seatTier = seatTier;
    }

    public BookingSeat(String bookingId, Integer seatId, BigDecimal unitPrice, String seatTier) {
        this(bookingId, null, seatId, unitPrice, seatTier);
    }

    public String getSeatCode() {
        return (rowChar != '\0' && seatNum > 0) ? String.valueOf(rowChar) + seatNum : "Seat #" + seatId;
    }

    public Integer getBookingSeatId() {
        return bookingSeatId;
    }

    public void setBookingSeatId(Integer bookingSeatId) {
        this.bookingSeatId = bookingSeatId;
    }

    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getSeatTier() {
        return seatTier;
    }

    public void setSeatTier(String seatTier) {
        this.seatTier = seatTier;
    }

    public char getRowChar() {
        return rowChar;
    }

    public void setRowChar(char rowChar) {
        this.rowChar = rowChar;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }
}
