package com.cinebook.service;

import com.cinebook.dao.ScreenDao;
import com.cinebook.dao.SeatDao;
import com.cinebook.dao.ShowDao;
import com.cinebook.domain.model.Screen;
import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import com.cinebook.domain.model.Show;
import com.cinebook.domain.strategy.CenterContiguousStrategy;
import com.cinebook.domain.strategy.SeatRecommendationStrategy;
import com.cinebook.domain.strategy.TierOptimizedContiguousStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SeatRecommendationService {

    private final SeatDao seatDao;
    private final ShowDao showDao;
    private final ScreenDao screenDao;
    private final Map<String, SeatRecommendationStrategy> strategies;

    @Autowired
    public SeatRecommendationService(
            SeatDao seatDao,
            ShowDao showDao,
            ScreenDao screenDao,
            CenterContiguousStrategy centerStrategy,
            TierOptimizedContiguousStrategy tierStrategy
    ) {
        this.seatDao = seatDao;
        this.showDao = showDao;
        this.screenDao = screenDao;
        this.strategies = Map.of(
                centerStrategy.getStrategyName(), centerStrategy,
                tierStrategy.getStrategyName(), tierStrategy
        );
    }

    public List<Seat> recommendSeats(int showId, int count, String preferredTierStr, String strategyName) {
        Show show = showDao.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        Screen screen = screenDao.findById(show.getScreenId())
                .orElseThrow(() -> new IllegalStateException("Screen not found for show: " + showId));

        List<Seat> availableSeats = seatDao.findAvailableSeatsForShow(showId);

        SeatType preferredTier = null;
        if (preferredTierStr != null && !preferredTierStr.trim().isEmpty() && !preferredTierStr.equalsIgnoreCase("ANY")) {
            preferredTier = SeatType.valueOf(preferredTierStr.trim().toUpperCase());
        }

        String key = (strategyName != null) ? strategyName.trim().toUpperCase() : "CENTER";
        SeatRecommendationStrategy strategy = strategies.getOrDefault(key, strategies.get("CENTER"));

        return strategy.recommendSeats(
                availableSeats,
                count,
                preferredTier,
                screen.getTotalRows(),
                screen.getSeatsPerRow()
        );
    }
}

