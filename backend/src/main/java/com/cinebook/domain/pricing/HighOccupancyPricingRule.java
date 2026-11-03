package com.cinebook.domain.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * OOP Polymorphism: HighOccupancyPricingRule.
 * 
 * Applies a +20% surge when show occupancy exceeds 80%.
 */
@Component
public class HighOccupancyPricingRule extends AbstractPricingRule {

    private final double threshold;

    public HighOccupancyPricingRule() {
        super("High Demand Surge (>80% Occupancy: +20%)", new BigDecimal("20.00"));
        this.threshold = 80.0;
    }

    public HighOccupancyPricingRule(BigDecimal customPercent, double threshold) {
        super("High Demand Surge (+" + customPercent + "%)", customPercent);
        this.threshold = threshold;
    }

    @Override
    public boolean isApplicable(PricingContext context) {
        return context != null && context.getOccupancyPercent() >= threshold;
    }
}

