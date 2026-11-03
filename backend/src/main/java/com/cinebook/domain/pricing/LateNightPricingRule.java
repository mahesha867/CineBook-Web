package com.cinebook.domain.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OOP Polymorphism: LateNightPricingRule.
 * 
 * Applies a -10% discount for screenings starting at or after 22:30.
 */
@Component
public class LateNightPricingRule extends AbstractPricingRule {

    public LateNightPricingRule() {
        super("Late Night Cine Discount (-10%)", new BigDecimal("-10.00"));
    }

    public LateNightPricingRule(BigDecimal customPercent) {
        super("Late Night Cine Discount (" + customPercent + "%)", customPercent);
    }

    @Override
    public boolean isApplicable(PricingContext context) {
        if (context == null || context.getShowTime() == null) {
            return false;
        }
        LocalDateTime time = context.getShowTime();
        int hour = time.getHour();
        int min = time.getMinute();

        return (hour == 22 && min >= 30) || hour >= 23;
    }
}

