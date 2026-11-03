package com.cinebook.domain.pricing;

import java.math.BigDecimal;

/**
 * OOP Abstraction: PricingRule Interface.
 * 
 * Part of the Strategy / Chain of Responsibility pattern for dynamic price adjustments.
 */
public interface PricingRule {

    /**
     * Determines whether this rule qualifies based on the context (time, day, occupancy).
     */
    boolean isApplicable(PricingContext context);

    /**
     * Computes the adjustment amount (positive for surcharge, negative for discount)
     * applied against the supplied base amount.
     */
    BigDecimal calculateAdjustment(PricingContext context, BigDecimal baseAmount);

    /**
     * User-facing description for the line-item checkout breakdown.
     */
    String getRuleName();
}

