package com.cinebook.service;

import com.cinebook.dao.CouponDao;
import com.cinebook.dao.PricingRuleDao;
import com.cinebook.domain.model.Coupon;
import com.cinebook.domain.model.PricingRuleEntity;
import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.Snack;
import com.cinebook.domain.pricing.*;
import com.cinebook.domain.valueobject.PriceBreakdown;
import com.cinebook.exception.InvalidCouponException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * OOP Design Pattern: Strategy & Chain-of-Responsibility Engine.
 * 
 * Computes pricing for tickets and snacks, applying dynamic rules and coupon discounts.
 * 
 * Academic Rules:
 * 1. Seat tiers define base price in Java (REGULAR 150, PREMIUM 250, VIP 400).
 * 2. Coupon (e.g. CINE10) discounts TICKET subtotal only. Concession snacks NEVER get ticket discounts.
 * 3. Final Total = (ticket subtotal - ticket discount) + snack subtotal.
 */
@Service
public class PricingEngine {

    private final List<PricingRule> defaultRules;
    private final CouponDao couponDao;
    private final PricingRuleDao pricingRuleDao;

    @Autowired
    public PricingEngine(
            List<PricingRule> defaultRules,
            CouponDao couponDao,
            PricingRuleDao pricingRuleDao
    ) {
        this.defaultRules = defaultRules;
        this.couponDao = couponDao;
        this.pricingRuleDao = pricingRuleDao;
    }

    /**
     * Calculates the transparent price breakdown.
     * 
     * @param seats selected seats
     * @param snacks selected snacks with quantities
     * @param showTime scheduled screening time
     * @param occupancyPercent current show occupancy percentage
     * @param showMultiplier base price multiplier from shows table
     * @param couponCode optional coupon string (e.g. "CINE10")
     * @return immutable PriceBreakdown value object
     */
    public PriceBreakdown calculatePriceBreakdown(
            List<Seat> seats,
            Map<Snack, Integer> snacks,
            LocalDateTime showTime,
            double occupancyPercent,
            BigDecimal showMultiplier,
            String couponCode
    ) {
        // 1. Calculate Base Ticket Total
        BigDecimal baseTicketTotal = BigDecimal.ZERO;
        if (seats != null) {
            for (Seat seat : seats) {
                BigDecimal seatBase = seat.getPrice();
                baseTicketTotal = baseTicketTotal.add(seatBase);
            }
        }

        // Apply show-level multiplier if present
        if (showMultiplier != null && showMultiplier.compareTo(BigDecimal.ONE) != 0) {
            baseTicketTotal = baseTicketTotal.multiply(showMultiplier).setScale(2, RoundingMode.HALF_UP);
        }

        // 2. Evaluate Dynamic Pricing Rules (Strategy / Chain)
        PricingContext context = new PricingContext(showTime, occupancyPercent, showMultiplier);
        Map<String, BigDecimal> ruleAdjustments = new LinkedHashMap<>();
        BigDecimal totalAdjustments = BigDecimal.ZERO;

        for (PricingRule rule : defaultRules) {
            if (rule.isApplicable(context)) {
                BigDecimal adj = rule.calculateAdjustment(context, baseTicketTotal);
                ruleAdjustments.put(rule.getRuleName(), adj);
                totalAdjustments = totalAdjustments.add(adj);
            }
        }

        // Ticket Subtotal after dynamic surge / late discounts
        BigDecimal ticketSubtotal = baseTicketTotal.add(totalAdjustments).setScale(2, RoundingMode.HALF_UP);
        if (ticketSubtotal.compareTo(BigDecimal.ZERO) < 0) {
            ticketSubtotal = BigDecimal.ZERO;
        }

        // 3. Evaluate Coupon Discount (Ticket Subtotal ONLY)
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            Coupon coupon = couponDao.findByCode(couponCode.trim())
                    .orElseThrow(() -> new InvalidCouponException("Invalid or expired coupon code: " + couponCode));

            if (!coupon.isValidNow()) {
                throw new InvalidCouponException("Coupon " + couponCode + " is not currently active.");
            }

            couponDiscount = ticketSubtotal.multiply(coupon.getDiscountPercent())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        BigDecimal discountedTicketTotal = ticketSubtotal.subtract(couponDiscount).setScale(2, RoundingMode.HALF_UP);
        if (discountedTicketTotal.compareTo(BigDecimal.ZERO) < 0) {
            discountedTicketTotal = BigDecimal.ZERO;
        }

        // 4. Calculate Concession Snacks Subtotal (No ticket discounts applied)
        BigDecimal snackTotal = BigDecimal.ZERO;
        if (snacks != null) {
            for (Map.Entry<Snack, Integer> entry : snacks.entrySet()) {
                Snack snack = entry.getKey();
                int qty = entry.getValue() != null ? entry.getValue() : 0;
                if (qty > 0) {
                    BigDecimal itemSubtotal = snack.getPrice().multiply(BigDecimal.valueOf(qty));
                    snackTotal = snackTotal.add(itemSubtotal);
                }
            }
        }
        snackTotal = snackTotal.setScale(2, RoundingMode.HALF_UP);

        // 5. Final Total = (ticket subtotal - ticket discount) + snack subtotal
        BigDecimal grandTotal = discountedTicketTotal.add(snackTotal).setScale(2, RoundingMode.HALF_UP);

        return new PriceBreakdown(
                baseTicketTotal,
                ruleAdjustments,
                ticketSubtotal,
                couponDiscount,
                discountedTicketTotal,
                snackTotal,
                grandTotal
        );
    }
}

