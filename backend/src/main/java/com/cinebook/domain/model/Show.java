package com.cinebook.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * OOP Entity: Show.
 */
public class Show {

    private Integer showId;
    private Integer movieId;
    private Integer screenId;
    private LocalDateTime showTime;
    private BigDecimal basePriceMultiplier = BigDecimal.ONE;
    private boolean isCancelled = false;

    // Joined fields for display convenience
    private String movieTitle;
    private String screenName;

    public Show() {
    }

    public Show(Integer showId, Integer movieId, Integer screenId, LocalDateTime showTime, BigDecimal basePriceMultiplier) {
        this.showId = showId;
        this.movieId = movieId;
        this.screenId = screenId;
        this.showTime = showTime;
        this.basePriceMultiplier = basePriceMultiplier != null ? basePriceMultiplier : BigDecimal.ONE;
        this.isCancelled = false;
    }

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public BigDecimal getBasePriceMultiplier() {
        return basePriceMultiplier;
    }

    public void setBasePriceMultiplier(BigDecimal basePriceMultiplier) {
        this.basePriceMultiplier = basePriceMultiplier;
    }

    public boolean isCancelled() {
        return isCancelled;
    }

    public void setCancelled(boolean cancelled) {
        isCancelled = cancelled;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }
}

