package com.cinebook.service;

import com.cinebook.dao.ScreenDao;
import com.cinebook.dao.SeatDao;
import com.cinebook.dao.SeatHoldDao;
import com.cinebook.dao.ShowDao;
import com.cinebook.domain.model.*;
import com.cinebook.dto.response.SeatMapResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShowService {

    private final ShowDao showDao;
    private final ScreenDao screenDao;
    private final SeatDao seatDao;
    private final SeatHoldDao seatHoldDao;

    @Autowired
    public ShowService(ShowDao showDao, ScreenDao screenDao, SeatDao seatDao, SeatHoldDao seatHoldDao) {
        this.showDao = showDao;
        this.screenDao = screenDao;
        this.seatDao = seatDao;
        this.seatHoldDao = seatHoldDao;
    }

    public List<Show> getUpcomingShowsByMovie(int movieId) {
        return showDao.findUpcomingByMovieId(movieId);
    }

    public List<Show> getAllUpcomingShows() {
        return showDao.findAllUpcoming();
    }

    public Optional<Show> getShowById(int showId) {
        return showDao.findById(showId);
    }

    public SeatMapResponse getSeatMap(int showId, String currentSessionId) {
        Show show = showDao.findById(showId)
                .orElseThrow(() -> new IllegalArgumentException("Show not found with id: " + showId));

        Screen screen = screenDao.findById(show.getScreenId())
                .orElseThrow(() -> new IllegalStateException("Screen not found for show: " + showId));

        List<Seat> allSeats = seatDao.findByScreenId(screen.getScreenId());
        Set<Integer> bookedSeatIds = seatDao.findBookedSeatIdsForShow(showId);
        Map<Integer, SeatHold> activeHolds = seatHoldDao.findActiveHoldsMapForShow(showId);

        List<SeatMapResponse.SeatStatusDTO> seatDtos = new ArrayList<>();
        int heldCount = 0;
        LocalDateTime now = LocalDateTime.now();

        for (Seat seat : allSeats) {
            String status;
            Long holdExpiresInSec = null;

            if (bookedSeatIds.contains(seat.getSeatId())) {
                status = "BOOKED";
            } else if (activeHolds.containsKey(seat.getSeatId())) {
                SeatHold hold = activeHolds.get(seat.getSeatId());
                heldCount++;
                if (hold.getExpiresAt().isAfter(now)) {
                    holdExpiresInSec = Math.max(0, Duration.between(now, hold.getExpiresAt()).getSeconds());
                }
                if (currentSessionId != null && currentSessionId.equals(hold.getSessionId())) {
                    status = "HELD_BY_YOU";
                } else {
                    status = "HELD";
                }
            } else {
                status = "AVAILABLE";
            }

            seatDtos.add(new SeatMapResponse.SeatStatusDTO(
                    seat.getSeatId(),
                    seat.getRowChar(),
                    seat.getSeatNum(),
                    seat.getSeatCode(),
                    seat.getTier(),
                    seat.getPrice(),
                    status,
                    holdExpiresInSec
            ));
        }

        SeatMapResponse resp = new SeatMapResponse();
        resp.setShowId(showId);
        resp.setMovieTitle(show.getMovieTitle());
        resp.setScreenName(show.getScreenName());
        resp.setShowTime(show.getShowTime());
        resp.setTotalCapacity(screen.getTotalCapacity());
        resp.setBookedSeatsCount(bookedSeatIds.size());
        resp.setHeldSeatsCount(heldCount);
        double occupancy = screen.getTotalCapacity() > 0
                ? ((double) bookedSeatIds.size() / screen.getTotalCapacity()) * 100.0
                : 0.0;
        resp.setOccupancyPercent(Math.round(occupancy * 100.0) / 100.0);
        resp.setSeats(seatDtos);
        return resp;
    }
}

