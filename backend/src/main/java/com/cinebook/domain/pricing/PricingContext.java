package com.cinebook.domain.pricing;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OOP Context Object: PricingContext.
 * 
 * Encapsulates environmental and transactional parameters needed
 * by dynamic pricing rules to evaluate eligibility and surcharge rates.
 */
public class PricingContext {

    private final LocalDateTime showTime;
    private final double occupancyPercent;
    private final BigDecimal basePriceMultiplier;

    public PricingContext(LocalDateTime showTime, double occupancyPercent, BigDecimal basePriceMultiplier) {
        this.showTime = showTime;
        this.occupancyPercent = occupancyPercent;
        this.basePriceMultiplier = basePriceMultiplier != null ? basePriceMultiplier : BigDecimal.ONE;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public double getOccupancyPercent() {
        return occupancyPercent;
    }

    public BigDecimal getBasePriceMultiplier() {
        return basePriceMultiplier;
    }
}

