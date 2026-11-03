package com.cinebook.controller;

import com.cinebook.dao.ScreenDao;
import com.cinebook.dao.SeatDao;
import com.cinebook.dao.ShowDao;
import com.cinebook.dao.SnackDao;
import com.cinebook.domain.model.Screen;
import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.Show;
import com.cinebook.domain.model.Snack;
import com.cinebook.domain.valueobject.PriceBreakdown;
import com.cinebook.dto.request.ValidateCouponRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.service.PricingEngine;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/coupons")
public class CouponController {

    private final PricingEngine pricingEngine;
    private final ShowDao showDao;
    private final ScreenDao screenDao;
    private final SeatDao seatDao;
    private final SnackDao snackDao;

    @Autowired
    public CouponController(PricingEngine pricingEngine, ShowDao showDao, ScreenDao screenDao, SeatDao seatDao, SnackDao snackDao) {
        this.pricingEngine = pricingEngine;
        this.showDao = showDao;
        this.screenDao = screenDao;
        this.seatDao = seatDao;
        this.snackDao = snackDao;
    }

    @PostMapping("/validate")
    public ResponseEntity<ApiResponse<PriceBreakdown>> validateCoupon(@Valid @RequestBody ValidateCouponRequest req) {
        Show show = showDao.findById(req.getShowId())
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + req.getShowId()));

        Screen screen = screenDao.findById(show.getScreenId())
                .orElseThrow(() -> new IllegalStateException("Screen not found for show: " + req.getShowId()));

        List<Seat> seats = seatDao.findByIds(req.getSeatIds());
        Set<Integer> bookedSeats = seatDao.findBookedSeatIdsForShow(req.getShowId());

        double occupancy = screen.getTotalCapacity() > 0
                ? ((double) bookedSeats.size() / screen.getTotalCapacity()) * 100.0
                : 0.0;

        Map<Snack, Integer> snackMap = new HashMap<>();
        if (req.getSnackOrders() != null) {
            for (Map.Entry<String, Integer> e : req.getSnackOrders().entrySet()) {
                snackDao.findById(e.getKey()).ifPresent(sn -> snackMap.put(sn, e.getValue()));
            }
        }

        PriceBreakdown breakdown = pricingEngine.calculatePriceBreakdown(
                seats,
                snackMap,
                show.getShowTime(),
                occupancy,
                show.getBasePriceMultiplier(),
                req.getCouponCode()
        );

        return ResponseEntity.ok(ApiResponse.ok("Coupon validated successfully", breakdown));
    }
}

