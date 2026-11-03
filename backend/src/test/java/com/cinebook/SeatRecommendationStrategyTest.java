package com.cinebook;

import com.cinebook.domain.model.Seat;
import com.cinebook.domain.model.SeatType;
import com.cinebook.domain.strategy.CenterContiguousStrategy;
import com.cinebook.domain.strategy.TierOptimizedContiguousStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SeatRecommendationStrategyTest {

    @Test
    @DisplayName("Strategy Pattern: CenterContiguousStrategy recommends middle row (C) and center seats (5, 6)")
    void testCenterContiguousStrategy() {
        CenterContiguousStrategy strategy = new CenterContiguousStrategy();

        // Create a 5x10 cinema grid (Rows A-E, seats 1-10)
        List<Seat> allSeats = new ArrayList<>();
        int id = 1;
        for (char r : List.of('A', 'B', 'C', 'D', 'E')) {
            for (int num = 1; num <= 10; num++) {
                allSeats.add(new Seat(id++, 1, r, num, SeatType.forRow(r)));
            }
        }

        // Recommend 2 seats together
        List<Seat> recommended = strategy.recommendSeats(allSeats, 2, null, 5, 10);

        assertThat(recommended).hasSize(2);
        // Center row is Row C (index 2 of 5 rows), center seats are 5 & 6
        assertThat(recommended.get(0).getRowChar()).isEqualTo('C');
        assertThat(recommended.get(1).getRowChar()).isEqualTo('C');
        assertThat(recommended.get(0).getSeatNum()).isEqualTo(5);
        assertThat(recommended.get(1).getSeatNum()).isEqualTo(6);
    }

    @Test
    @DisplayName("Strategy Pattern: TierOptimizedContiguousStrategy prioritizes VIP tier (Row E)")
    void testTierOptimizedStrategy() {
        CenterContiguousStrategy centerStrategy = new CenterContiguousStrategy();
        TierOptimizedContiguousStrategy strategy = new TierOptimizedContiguousStrategy(centerStrategy);

        List<Seat> allSeats = new ArrayList<>();
        int id = 1;
        for (char r : List.of('A', 'B', 'C', 'D', 'E')) {
            for (int num = 1; num <= 10; num++) {
                allSeats.add(new Seat(id++, 1, r, num, SeatType.forRow(r)));
            }
        }

        // Request 3 VIP seats
        List<Seat> recommended = strategy.recommendSeats(allSeats, 3, SeatType.VIP, 5, 10);

        assertThat(recommended).hasSize(3);
        // All recommended seats must be VIP (Row E)
        assertThat(recommended).allMatch(s -> s.getTier() == SeatType.VIP);
        assertThat(recommended).allMatch(s -> s.getRowChar() == 'E');
        // Contiguous
        assertThat(recommended.get(1).getSeatNum()).isEqualTo(recommended.get(0).getSeatNum() + 1);
        assertThat(recommended.get(2).getSeatNum()).isEqualTo(recommended.get(1).getSeatNum() + 1);
    }
}

