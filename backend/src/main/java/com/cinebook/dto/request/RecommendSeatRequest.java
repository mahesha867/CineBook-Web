package com.cinebook.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class RecommendSeatRequest {

    @Min(value = 1, message = "Count must be at least 1")
    @Max(value = 10, message = "Count cannot exceed 10")
    private int count = 2;

    private String preferredTier; // REGULAR, PREMIUM, VIP, ANY

    private String strategy = "CENTER"; // CENTER or TIER_OPTIMIZED

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getPreferredTier() {
        return preferredTier;
    }

    public void setPreferredTier(String preferredTier) {
        this.preferredTier = preferredTier;
    }

    public String getStrategy() {
        return strategy;
    }

    public void setStrategy(String strategy) {
        this.strategy = strategy;
    }
}

