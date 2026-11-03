package com.cinebook.domain.model;

import java.time.LocalDateTime;

/**
 * OOP Entity: WaitlistEntry.
 * 
 * Tracks customers queueing for sold-out cinema shows.
 */
public class WaitlistEntry {

    private Integer waitlistId;
    private Integer showId;
    private Integer userId;
    private int seatsRequested;
    private String preferredTier = "ANY";
    private WaitlistStatus status = WaitlistStatus.WAITING;
    private LocalDateTime createdAt;
    private LocalDateTime promotedAt;

    // Joined fields
    private String customerName;
    private String customerEmail;
    private String movieTitle;

    public WaitlistEntry() {
    }

    public WaitlistEntry(Integer showId, Integer userId, int seatsRequested, String preferredTier) {
        this.showId = showId;
        this.userId = userId;
        this.seatsRequested = seatsRequested;
        this.preferredTier = preferredTier != null ? preferredTier : "ANY";
        this.status = WaitlistStatus.WAITING;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getWaitlistId() {
        return waitlistId;
    }

    public void setWaitlistId(Integer waitlistId) {
        this.waitlistId = waitlistId;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public int getSeatsRequested() {
        return seatsRequested;
    }

    public void setSeatsRequested(int seatsRequested) {
        this.seatsRequested = seatsRequested;
    }

    public String getPreferredTier() {
        return preferredTier;
    }

    public void setPreferredTier(String preferredTier) {
        this.preferredTier = preferredTier;
    }

    public WaitlistStatus getStatus() {
        return status;
    }

    public void setStatus(WaitlistStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getPromotedAt() {
        return promotedAt;
    }

    public void setPromotedAt(LocalDateTime promotedAt) {
        this.promotedAt = promotedAt;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
}

