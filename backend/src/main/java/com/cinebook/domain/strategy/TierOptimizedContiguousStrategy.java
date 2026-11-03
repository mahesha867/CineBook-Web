package com.cinebook.domain.strategy;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OOP Strategy Implementation: TierOptimizedContiguousStrategy.
 * 
 * Enforces tier prioritization first. Finds contiguous blocks within the desired tier
 * (e.g. VIP or PREMIUM), falling back gracefully to other tiers if the requested tier
 * cannot accommodate the group size.
 */
@Component("tierOptimizedContiguousStrategy")
public class TierOptimizedContiguousStrategy implements SeatRecommendationStrategy {

    private final CenterContiguousStrategy fallbackCenterStrategy;

    @Autowired
    public TierOptimizedContiguousStrategy(CenterContiguousStrategy fallbackCenterStrategy) {
        this.fallbackCenterStrategy = fallbackCenterStrategy;
    }

    @Override
    public List<Seat> recommendSeats(
            List<Seat> availableSeats,
            int count,
            SeatType preferredTier,
            int totalRows,
            int seatsPerRow
    ) {
        if (availableSeats == null || availableSeats.isEmpty() || count <= 0) {
            return Collections.emptyList();
        }

        if (preferredTier == null) {
            return fallbackCenterStrategy.recommendSeats(availableSeats, count, null, totalRows, seatsPerRow);
        }

        // Filter seats belonging strictly to requested tier
        List<Seat> tierSeats = availableSeats.stream()
                .filter(s -> s.getTier() == preferredTier)
                .collect(Collectors.toList());

        List<Seat> result = fallbackCenterStrategy.recommendSeats(tierSeats, count, preferredTier, totalRows, seatsPerRow);
        if (!result.isEmpty()) {
            return result;
        }

        // Graceful fallback to all available seats if preferred tier doesn't have N contiguous seats
        return fallbackCenterStrategy.recommendSeats(availableSeats, count, preferredTier, totalRows, seatsPerRow);
    }

    @Override
    public String getStrategyName() {
        return "TIER_OPTIMIZED";
    }
}

