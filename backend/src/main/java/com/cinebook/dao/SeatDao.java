package com.cinebook.dao;

import com.cinebook.domain.model.Seat;

import java.util.List;
import java.util.Set;

public interface SeatDao {
    List<Seat> findByScreenId(int screenId);
    List<Seat> findByIds(List<Integer> seatIds);
    Set<Integer> findBookedSeatIdsForShow(int showId);
    List<Seat> findAvailableSeatsForShow(int showId);
}

