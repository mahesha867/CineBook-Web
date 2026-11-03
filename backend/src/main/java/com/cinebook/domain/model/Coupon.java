package com.cinebook.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OOP Entity: Coupon.
 */
public class Coupon {

    private String couponCode;
    private BigDecimal discountPercent;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private boolean isActive = true;

    public Coupon() {
    }

    public Coupon(String couponCode, BigDecimal discountPercent, LocalDateTime validFrom, LocalDateTime validUntil, boolean isActive) {
        this.couponCode = couponCode;
        this.discountPercent = discountPercent;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        this.isActive = isActive;
    }

    public boolean isValidNow() {
        LocalDateTime now = LocalDateTime.now();
        return isActive && (validFrom == null || !now.isBefore(validFrom)) && (validUntil == null || !now.isAfter(validUntil));
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public BigDecimal getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(BigDecimal discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}

