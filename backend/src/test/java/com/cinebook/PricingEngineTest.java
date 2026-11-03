package com.cinebook;

import com.cinebook.dao.CouponDao;
import com.cinebook.dao.PricingRuleDao;
import com.cinebook.domain.model.Coupon;
import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import com.cinebook.domain.model.Snack;
import com.cinebook.domain.model.SnackCategory;
import com.cinebook.domain.pricing.*;
import com.cinebook.domain.valueobject.PriceBreakdown;
import com.cinebook.service.PricingEngine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * Unit Test verifying Academic Business Rules in PricingEngine:
 * 1. Coupon applies strictly to tickets only.
 * 2. Concession snacks never receive ticket discounts.
 * 3. Final Total = (ticket subtotal - ticket discount) + snack subtotal.
 */
class PricingEngineTest {

    private CouponDao couponDao;
    private PricingRuleDao pricingRuleDao;
    private PricingEngine pricingEngine;

    @BeforeEach
    void setUp() {
        couponDao = Mockito.mock(CouponDao.class);
        pricingRuleDao = Mockito.mock(PricingRuleDao.class);

        // Standard rules
        List<PricingRule> rules = List.of(
                new WeekendPricingRule(),
                new LateNightPricingRule(),
                new HighOccupancyPricingRule()
        );

        pricingEngine = new PricingEngine(rules, couponDao, pricingRuleDao);
    }

    @Test
    @DisplayName("Academic Rule: Coupon CINE10 (10%) applies strictly to ticket subtotal, 0% to snacks")
    void testCouponAppliesToTicketsOnly() {
        // Mock CINE10 coupon with 10% discount
        Coupon cine10 = new Coupon("CINE10", new BigDecimal("10.00"), LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1), true);
        when(couponDao.findByCode(eq("CINE10"))).thenReturn(Optional.of(cine10));

        // 2 Regular seats (Row A: 150 each -> Base tickets = 300)
        Seat seat1 = new Seat(1, 1, 'A', 1, SeatType.REGULAR);
        Seat seat2 = new Seat(2, 1, 'A', 2, SeatType.REGULAR);
        List<Seat> seats = List.of(seat1, seat2);

        // Concessions: 1 Popcorn (120) + 1 Coke (80) = 200 snack subtotal
        Snack popcorn = new Snack("S001", "Popcorn", new BigDecimal("120.00"), SnackCategory.FOOD, true);
        Snack coke = new Snack("S007", "Coke", new BigDecimal("80.00"), SnackCategory.BEVERAGE, true);
        Map<Snack, Integer> snacks = Map.of(popcorn, 1, coke, 1);

        // Wednesday 14:00 (no weekend or late night surcharge), 30% occupancy
        LocalDateTime wedMatinee = LocalDateTime.of(2026, 11, 4, 14, 0); // Wednesday

        PriceBreakdown breakdown = pricingEngine.calculatePriceBreakdown(
                seats,
                snacks,
                wedMatinee,
                30.0,
                BigDecimal.ONE,
                "CINE10"
        );

        // Verification:
        // Base Tickets = 300.00
        assertThat(breakdown.getBaseTicketTotal()).isEqualByComparingTo("300.00");
        assertThat(breakdown.getTicketSubtotal()).isEqualByComparingTo("300.00");

        // Coupon Discount = 10% of 300.00 = 30.00
        assertThat(breakdown.getCouponDiscount()).isEqualByComparingTo("30.00");
        assertThat(breakdown.getDiscountedTicketTotal()).isEqualByComparingTo("270.00");

        // Snacks = 200.00 (Zero discount applied!)
        assertThat(breakdown.getSnackTotal()).isEqualByComparingTo("200.00");

        // Final = (300 - 30) + 200 = 470.00
        assertThat(breakdown.getGrandTotal()).isEqualByComparingTo("470.00");
    }

    @Test
    @DisplayName("Dynamic Pricing: Weekend +15% surge applies on Saturday screening")
    void testWeekendSurchargeApplication() {
        when(couponDao.findByCode(Mockito.anyString())).thenReturn(Optional.empty());

        // 1 VIP seat (Row E: Rs. 400)
        Seat vip = new Seat(5, 1, 'E', 5, SeatType.VIP);
        List<Seat> seats = List.of(vip);

        // Saturday evening show
        LocalDateTime saturdayShow = LocalDateTime.of(2026, 11, 7, 19, 0); // Saturday

        PriceBreakdown breakdown = pricingEngine.calculatePriceBreakdown(
                seats,
                Map.of(),
                saturdayShow,
                50.0,
                BigDecimal.ONE,
                null
        );

        // Base = 400.00
        assertThat(breakdown.getBaseTicketTotal()).isEqualByComparingTo("400.00");

        // Weekend surcharge = 15% of 400 = 60.00
        assertThat(breakdown.getRuleAdjustments()).containsKey("Weekend Peak Surcharge (+15%)");
        assertThat(breakdown.getRuleAdjustments().get("Weekend Peak Surcharge (+15%)")).isEqualByComparingTo("60.00");

        // Ticket Subtotal = 460.00
        assertThat(breakdown.getTicketSubtotal()).isEqualByComparingTo("460.00");
        assertThat(breakdown.getGrandTotal()).isEqualByComparingTo("460.00");
    }

    @Test
    @DisplayName("Dynamic Pricing: Late night discount (-10%) applies for shows starting at 23:00")
    void testLateNightDiscount() {
        when(couponDao.findByCode(Mockito.anyString())).thenReturn(Optional.empty());

        // 1 Premium seat (Row C: Rs. 250)
        Seat premium = new Seat(15, 1, 'C', 4, SeatType.PREMIUM);
        List<Seat> seats = List.of(premium);

        // Tuesday 23:00 Late night show
        LocalDateTime tuesdayLate = LocalDateTime.of(2026, 11, 3, 23, 0);

        PriceBreakdown breakdown = pricingEngine.calculatePriceBreakdown(
                seats,
                Map.of(),
                tuesdayLate,
                20.0,
                BigDecimal.ONE,
                null
        );

        // Base = 250.00
        // Late night discount = -10% of 250 = -25.00
        assertThat(breakdown.getRuleAdjustments()).containsKey("Late Night Cine Discount (-10%)");
        assertThat(breakdown.getRuleAdjustments().get("Late Night Cine Discount (-10%)")).isEqualByComparingTo("-25.00");

        // Ticket Subtotal = 225.00
        assertThat(breakdown.getTicketSubtotal()).isEqualByComparingTo("225.00");
        assertThat(breakdown.getGrandTotal()).isEqualByComparingTo("225.00");
    }
}

