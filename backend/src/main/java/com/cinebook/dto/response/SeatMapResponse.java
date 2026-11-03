package com.cinebook.dto.response;

import com.cinebook.domain.model.SeatType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SeatMapResponse {

    private int showId;
    private String movieTitle;
    private String screenName;
    private LocalDateTime showTime;
    private int totalCapacity;
    private int bookedSeatsCount;
    private int heldSeatsCount;
    private double occupancyPercent;
    private List<SeatStatusDTO> seats;

    public SeatMapResponse() {
    }

    public static class SeatStatusDTO {
        private int seatId;
        private char rowChar;
        private int seatNum;
        private String seatCode;
        private SeatType tier;
        private BigDecimal basePrice;
        private String status; // AVAILABLE, HELD, HELD_BY_YOU, BOOKED
        private Long holdExpiresInSeconds;

        public SeatStatusDTO() {
        }

        public SeatStatusDTO(int seatId, char rowChar, int seatNum, String seatCode, SeatType tier, BigDecimal basePrice, String status, Long holdExpiresInSeconds) {
            this.seatId = seatId;
            this.rowChar = rowChar;
            this.seatNum = seatNum;
            this.seatCode = seatCode;
            this.tier = tier;
            this.basePrice = basePrice;
            this.status = status;
            this.holdExpiresInSeconds = holdExpiresInSeconds;
        }

        public int getSeatId() {
            return seatId;
        }

        public void setSeatId(int seatId) {
            this.seatId = seatId;
        }

        public char getRowChar() {
            return rowChar;
        }

        public void setRowChar(char rowChar) {
            this.rowChar = rowChar;
        }

        public int getSeatNum() {
            return seatNum;
        }

        public void setSeatNum(int seatNum) {
            this.seatNum = seatNum;
        }

        public String getSeatCode() {
            return seatCode;
        }

        public void setSeatCode(String seatCode) {
            this.seatCode = seatCode;
        }

        public SeatType getTier() {
            return tier;
        }

        public void setTier(SeatType tier) {
            this.tier = tier;
        }

        public BigDecimal getBasePrice() {
            return basePrice;
        }

        public void setBasePrice(BigDecimal basePrice) {
            this.basePrice = basePrice;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Long getHoldExpiresInSeconds() {
            return holdExpiresInSeconds;
        }

        public void setHoldExpiresInSeconds(Long holdExpiresInSeconds) {
            this.holdExpiresInSeconds = holdExpiresInSeconds;
        }
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
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

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public int getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(int totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public int getBookedSeatsCount() {
        return bookedSeatsCount;
    }

    public void setBookedSeatsCount(int bookedSeatsCount) {
        this.bookedSeatsCount = bookedSeatsCount;
    }

    public int getHeldSeatsCount() {
        return heldSeatsCount;
    }

    public void setHeldSeatsCount(int heldSeatsCount) {
        this.heldSeatsCount = heldSeatsCount;
    }

    public double getOccupancyPercent() {
        return occupancyPercent;
    }

    public void setOccupancyPercent(double occupancyPercent) {
        this.occupancyPercent = occupancyPercent;
    }

    public List<SeatStatusDTO> getSeats() {
        return seats;
    }

    public void setSeats(List<SeatStatusDTO> seats) {
        this.seats = seats;
    }
}

