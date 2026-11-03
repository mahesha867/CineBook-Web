package com.cinebook.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class HoldSeatRequest {

    @NotEmpty(message = "seatIds must not be empty")
    private List<Integer> seatIds;

    @NotNull(message = "sessionId is required")
    private String sessionId;

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}

