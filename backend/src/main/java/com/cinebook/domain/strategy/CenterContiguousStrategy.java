package com.cinebook.domain.strategy;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * OOP Strategy Implementation: CenterContiguousStrategy.
 * 
 * Prioritizes contiguous seating blocks situated directly in the center of the auditorium
 * (optimal acoustic and visual sweet spot), gracefully incorporating tier preferences.
 */
@Component("centerContiguousStrategy")
public class CenterContiguousStrategy implements SeatRecommendationStrategy {

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

        // Group seats by row
        Map<Character, List<Seat>> byRow = availableSeats.stream()
                .collect(Collectors.groupingBy(Seat::getRowChar));

        double idealRowIdx = (totalRows - 1) / 2.0; // e.g. Row C (index 2) in 5-row screen
        double idealCol = (seatsPerRow + 1) / 2.0; // e.g. 5.5 in 10-seat row

        CandidateBest candidate = null;

        for (Map.Entry<Character, List<Seat>> entry : byRow.entrySet()) {
            char rowChar = entry.getKey();
            int rowIdx = rowChar - 'A';
            List<Seat> rowSeats = entry.getValue();
            rowSeats.sort(Comparator.comparingInt(Seat::getSeatNum));

            // Find all contiguous windows of size 'count'
            for (int i = 0; i <= rowSeats.size() - count; i++) {
                boolean isContiguous = true;
                for (int j = 0; j < count - 1; j++) {
                    if (rowSeats.get(i + j + 1).getSeatNum() != rowSeats.get(i + j).getSeatNum() + 1) {
                        isContiguous = false;
                        break;
                    }
                }

                if (isContiguous) {
                    List<Seat> block = rowSeats.subList(i, i + count);
                    double avgCol = block.stream().mapToInt(Seat::getSeatNum).average().orElse(idealCol);
                    
                    // Score: row distance weight + col distance
                    double distance = Math.abs(rowIdx - idealRowIdx) * 2.0 + Math.abs(avgCol - idealCol);

                    // If preferredTier was specified and this row doesn't match, apply penalty
                    if (preferredTier != null && block.get(0).getTier() != preferredTier) {
                        distance += 15.0; // Prefer matched tier, but fallback if unavailable
                    }

                    if (candidate == null || distance < candidate.score) {
                        candidate = new CandidateBest(new ArrayList<>(block), distance);
                    }
                }
            }
        }

        return candidate != null ? candidate.seats : Collections.emptyList();
    }

    @Override
    public String getStrategyName() {
        return "CENTER";
    }

    private record CandidateBest(List<Seat> seats, double score) {}
}

