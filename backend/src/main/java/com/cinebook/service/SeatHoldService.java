package com.cinebook.service;

import com.cinebook.dao.SeatDao;
import com.cinebook.dao.SeatHoldDao;
import com.cinebook.domain.model.SeatHold;
import com.cinebook.exception.SeatHeldException;
import com.cinebook.exception.SeatUnavailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Service managing temporary 5-minute seat reservations and background cleanup.
 */
@Service
public class SeatHoldService {

    private static final Logger log = LoggerFactory.getLogger(SeatHoldService.class);

    private final SeatHoldDao seatHoldDao;
    private final SeatDao seatDao;

    @Value("${cinebook.booking.hold-duration-minutes:5}")
    private int holdDurationMinutes;

    @Autowired
    public SeatHoldService(SeatHoldDao seatHoldDao, SeatDao seatDao) {
        this.seatHoldDao = seatHoldDao;
        this.seatDao = seatDao;
    }

    @Transactional
    public void holdSeats(int showId, List<Integer> seatIds, String sessionId) {
        if (seatIds == null || seatIds.isEmpty()) {
            return;
        }

        // 1. Verify none are confirmed booked
        Set<Integer> bookedSeats = seatDao.findBookedSeatIdsForShow(showId);
        for (Integer sid : seatIds) {
            if (bookedSeats.contains(sid)) {
                throw new SeatUnavailableException("Seat #" + sid + " is already booked for this show.");
            }
        }

        // 2. Verify none are held by another user session
        Map<Integer, SeatHold> activeHolds = seatHoldDao.findActiveHoldsMapForShow(showId);
        for (Integer sid : seatIds) {
            if (activeHolds.containsKey(sid)) {
                SeatHold hold = activeHolds.get(sid);
                if (!hold.getSessionId().equals(sessionId)) {
                    throw new SeatHeldException("Seat #" + sid + " is currently reserved by another customer.");
                }
            }
        }

        // 3. Persist or extend the 5-minute hold
        seatHoldDao.holdSeats(showId, seatIds, sessionId, holdDurationMinutes);
    }

    public void releaseSeats(int showId, List<Integer> seatIds, String sessionId) {
        seatHoldDao.releaseHolds(showId, seatIds, sessionId);
    }

    /**
     * Background scheduled task running every 15 seconds to release expired holds.
     */
    @Scheduled(fixedRate = 15000)
    public void cleanupExpiredHolds() {
        try {
            int released = seatHoldDao.releaseExpiredHolds();
            if (released > 0) {
                log.info("Released {} expired seat holds.", released);
            }
        } catch (Exception e) {
            log.error("Error during scheduled seat hold cleanup: {}", e.getMessage());
        }
    }
}

