package com.cinebook.domain.model;

import java.time.LocalDateTime;

/**
 * OOP Entity: SeatHold.
 * 
 * Represents a temporary 5-minute reservation on a cinema seat.
 */
public class SeatHold {

    private Integer holdId;
    private Integer showId;
    private Integer seatId;
    private String sessionId;
    private LocalDateTime heldAt;
    private LocalDateTime expiresAt;

    public SeatHold() {
    }

    public SeatHold(Integer showId, Integer seatId, String sessionId, LocalDateTime expiresAt) {
        this.showId = showId;
        this.seatId = seatId;
        this.sessionId = sessionId;
        this.heldAt = LocalDateTime.now();
        this.expiresAt = expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(LocalDateTime.now());
    }

    public Integer getHoldId() {
        return holdId;
    }

    public void setHoldId(Integer holdId) {
        this.holdId = holdId;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public LocalDateTime getHeldAt() {
        return heldAt;
    }

    public void setHeldAt(LocalDateTime heldAt) {
        this.heldAt = heldAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}

