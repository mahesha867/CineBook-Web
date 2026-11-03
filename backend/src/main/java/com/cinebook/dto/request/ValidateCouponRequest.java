package com.cinebook.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Map;

public class ValidateCouponRequest {

    @NotNull(message = "showId is required")
    private Integer showId;

    @NotEmpty(message = "seatIds must not be empty")
    private List<Integer> seatIds;

    @NotBlank(message = "couponCode is required")
    private String couponCode;

    private Map<String, Integer> snackOrders;

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public List<Integer> getSeatIds() {
        return seatIds;
    }

    public void setSeatIds(List<Integer> seatIds) {
        this.seatIds = seatIds;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Map<String, Integer> getSnackOrders() {
        return snackOrders;
    }

    public void setSnackOrders(Map<String, Integer> snackOrders) {
        this.snackOrders = snackOrders;
    }
}

