package com.cinebook.domain.pricing;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * OOP Inheritance: AbstractPricingRule.
 * 
 * Provides reusable percentage computation logic for concrete dynamic pricing strategies.
 */
public abstract class AbstractPricingRule implements PricingRule {

    protected final String ruleName;
    protected final BigDecimal adjustmentPercent;

    protected AbstractPricingRule(String ruleName, BigDecimal adjustmentPercent) {
        this.ruleName = ruleName;
        this.adjustmentPercent = adjustmentPercent;
    }

    @Override
    public BigDecimal calculateAdjustment(PricingContext context, BigDecimal baseAmount) {
        if (baseAmount == null || BigDecimal.ZERO.compareTo(baseAmount) >= 0) {
            return BigDecimal.ZERO;
        }
        return baseAmount.multiply(adjustmentPercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public String getRuleName() {
        return ruleName;
    }
}

