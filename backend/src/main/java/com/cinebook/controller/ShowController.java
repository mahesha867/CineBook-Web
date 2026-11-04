package com.cinebook.controller;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.Show;
import com.cinebook.dto.request.HoldSeatRequest;
import com.cinebook.dto.request.RecommendSeatRequest;
import com.cinebook.dto.request.ReleaseHoldRequest;
import com.cinebook.dto.response.ApiResponse;
import com.cinebook.dto.response.SeatMapResponse;
import com.cinebook.service.SeatHoldService;
import com.cinebook.service.SeatRecommendationService;
import com.cinebook.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;
    private final SeatHoldService seatHoldService;
    private final SeatRecommendationService seatRecommendationService;

    @Autowired
    public ShowController(
            ShowService showService,
            SeatHoldService seatHoldService,
            SeatRecommendationService seatRecommendationService
    ) {
        this.showService = showService;
        this.seatHoldService = seatHoldService;
        this.seatRecommendationService = seatRecommendationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Show>>> getAllShows() {
        return ResponseEntity.ok(ApiResponse.ok(showService.getAllUpcomingShows()));
    }

    @GetMapping("/{showId}")
    public ResponseEntity<ApiResponse<Show>> getShowById(@PathVariable int showId) {
        return showService.getShowById(showId)
                .map(s -> ResponseEntity.ok(ApiResponse.ok(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{showId}/seats")
    public ResponseEntity<ApiResponse<SeatMapResponse>> getSeatMap(
            @PathVariable int showId,
            @RequestParam(required = false) String sessionId
    ) {
        SeatMapResponse seatMap = showService.getSeatMap(showId, sessionId);
        return ResponseEntity.ok(ApiResponse.ok(seatMap));
    }

    @PostMapping("/{showId}/hold-seats")
    public ResponseEntity<ApiResponse<String>> holdSeats(
            @PathVariable int showId,
            @Valid @RequestBody HoldSeatRequest req
    ) {
        seatHoldService.holdSeats(showId, req.getSeatIds(), req.getSessionId());
        return ResponseEntity.ok(ApiResponse.ok("Seats held successfully for 5 minutes", req.getSessionId()));
    }

    @PostMapping("/{showId}/release-holds")
    public ResponseEntity<ApiResponse<String>> releaseHolds(
            @PathVariable int showId,
            @Valid @RequestBody ReleaseHoldRequest req
    ) {
        seatHoldService.releaseSeats(showId, req.getSeatIds(), req.getSessionId());
        return ResponseEntity.ok(ApiResponse.ok("Holds released successfully", null));
    }

    @PostMapping("/{showId}/recommend-seats")
    public ResponseEntity<ApiResponse<List<Seat>>> recommendSeats(
            @PathVariable int showId,
            @Valid @RequestBody RecommendSeatRequest req
    ) {
        List<Seat> recommended = seatRecommendationService.recommendSeats(
                showId,
                req.getCount(),
                req.getPreferredTier(),
                req.getStrategy()
        );
        return ResponseEntity.ok(ApiResponse.ok(recommended));
    }
}
