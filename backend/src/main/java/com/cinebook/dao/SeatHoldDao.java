package com.cinebook.dao;

import com.cinebook.domain.model.SeatHold;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface SeatHoldDao {
    void holdSeats(int showId, List<Integer> seatIds, String sessionId, int durationMinutes);
    Set<Integer> findActiveHeldSeatIdsForShow(int showId);
    Map<Integer, SeatHold> findActiveHoldsMapForShow(int showId);
    boolean areAllSeatsHeldBySession(int showId, List<Integer> seatIds, String sessionId);
    void releaseHolds(int showId, List<Integer> seatIds, String sessionId);
    int releaseExpiredHolds();
}

