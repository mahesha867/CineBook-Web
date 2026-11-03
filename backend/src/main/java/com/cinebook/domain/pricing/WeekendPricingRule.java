package com.cinebook.domain.pricing;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDateTime;

/**
 * OOP Polymorphism: WeekendPricingRule.
 * 
 * Applies a +15% surge for Friday evening (>= 18:00), Saturday, and Sunday screenings.
 */
@Component
public class WeekendPricingRule extends AbstractPricingRule {

    public WeekendPricingRule() {
        super("Weekend Peak Surcharge (+15%)", new BigDecimal("15.00"));
    }

    public WeekendPricingRule(BigDecimal customPercent) {
        super("Weekend Peak Surcharge (+" + customPercent + "%)", customPercent);
    }

    @Override
    public boolean isApplicable(PricingContext context) {
        if (context == null || context.getShowTime() == null) {
            return false;
        }
        LocalDateTime time = context.getShowTime();
        DayOfWeek dow = time.getDayOfWeek();

        if (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY) {
            return true;
        }
        return dow == DayOfWeek.FRIDAY && time.getHour() >= 18;
    }
}

