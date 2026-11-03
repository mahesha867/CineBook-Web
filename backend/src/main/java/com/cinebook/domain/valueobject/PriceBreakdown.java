package com.cinebook.domain.valueobject;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

/**
 * OOP Value Object: PriceBreakdown.
 * 
 * Demonstrates Immutability and Encapsulation:
 * - All fields are private final.
 * - Map of dynamic rule adjustments is wrapped in an unmodifiable map.
 * - Exposes zero mutator methods.
 * - Represents the complete, transparent price calculation for customer checkout.
 */
public final class PriceBreakdown {

    private final BigDecimal baseTicketTotal;
    private final Map<String, BigDecimal> ruleAdjustments; // e.g. {"Weekend Surcharge (+15%)": 45.00}
    private final BigDecimal ticketSubtotal;
    private final BigDecimal couponDiscount;
    private final BigDecimal discountedTicketTotal;
    private final BigDecimal snackTotal;
    private final BigDecimal grandTotal;

    public PriceBreakdown(
            BigDecimal baseTicketTotal,
            Map<String, BigDecimal> ruleAdjustments,
            BigDecimal ticketSubtotal,
            BigDecimal couponDiscount,
            BigDecimal discountedTicketTotal,
            BigDecimal snackTotal,
            BigDecimal grandTotal
    ) {
        this.baseTicketTotal = Objects.requireNonNullElse(baseTicketTotal, BigDecimal.ZERO);
        this.ruleAdjustments = ruleAdjustments != null ? Collections.unmodifiableMap(ruleAdjustments) : Collections.emptyMap();
        this.ticketSubtotal = Objects.requireNonNullElse(ticketSubtotal, BigDecimal.ZERO);
        this.couponDiscount = Objects.requireNonNullElse(couponDiscount, BigDecimal.ZERO);
        this.discountedTicketTotal = Objects.requireNonNullElse(discountedTicketTotal, BigDecimal.ZERO);
        this.snackTotal = Objects.requireNonNullElse(snackTotal, BigDecimal.ZERO);
        this.grandTotal = Objects.requireNonNullElse(grandTotal, BigDecimal.ZERO);
    }

    public BigDecimal getBaseTicketTotal() {
        return baseTicketTotal;
    }

    public Map<String, BigDecimal> getRuleAdjustments() {
        return ruleAdjustments;
    }

    public BigDecimal getTicketSubtotal() {
        return ticketSubtotal;
    }

    public BigDecimal getCouponDiscount() {
        return couponDiscount;
    }

    public BigDecimal getDiscountedTicketTotal() {
        return discountedTicketTotal;
    }

    public BigDecimal getSnackTotal() {
        return snackTotal;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    @Override
    public String toString() {
        return "PriceBreakdown{" +
                "baseTickets=" + baseTicketTotal +
                ", adjustments=" + ruleAdjustments +
                ", ticketSubtotal=" + ticketSubtotal +
                ", couponDiscount=" + couponDiscount +
                ", discountedTickets=" + discountedTicketTotal +
                ", snacks=" + snackTotal +
                ", grandTotal=" + grandTotal +
                '}';
    }
}

