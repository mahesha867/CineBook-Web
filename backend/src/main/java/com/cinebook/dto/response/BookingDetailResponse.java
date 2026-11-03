package com.cinebook.dto.response;

import com.cinebook.domain.model.Booking;

public class BookingDetailResponse {

    private Booking booking;
    private String qrCodeDataUrl;

    public BookingDetailResponse() {
    }

    public BookingDetailResponse(Booking booking, String qrCodeDataUrl) {
        this.booking = booking;
        this.qrCodeDataUrl = qrCodeDataUrl;
    }

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    public String getQrCodeDataUrl() {
        return qrCodeDataUrl;
    }

    public void setQrCodeDataUrl(String qrCodeDataUrl) {
        this.qrCodeDataUrl = qrCodeDataUrl;
    }
}

