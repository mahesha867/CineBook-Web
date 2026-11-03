package com.cinebook.domain.strategy;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;

import java.util.List;
import java.util.Set;

/**
 * OOP Design Pattern: Strategy Pattern.
 * 
 * Defines an algorithmic contract for finding the best contiguous block of seats
 * given a target count, available seats, and customer preferences.
 */
public interface SeatRecommendationStrategy {

    /**
     * Finds and returns the best contiguous list of seats.
     * 
     * @param availableSeats all currently free (not booked, not held) seats
     * @param count number of adjacent seats desired
     * @param preferredTier requested tier (may be null for any tier)
     * @param totalRows total rows in screen
     * @param seatsPerRow total seats per row
     * @return recommended list of seats, or empty list if no suitable block found
     */
    List<Seat> recommendSeats(
            List<Seat> availableSeats,
            int count,
            SeatType preferredTier,
            int totalRows,
            int seatsPerRow
    );

    /**
     * Unique identifier name for this strategy.
     */
    String getStrategyName();
}

