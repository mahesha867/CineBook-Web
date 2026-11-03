package com.cinebook.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * OOP Entity: Seat.
 * 
 * Implements the {@link Bookable} interface, demonstrating polymorphism.
 * Encapsulates physical screen seat attributes and maps row characters
 * to their respective {@link SeatType}.
 */
public class Seat implements Bookable {

    private Integer seatId;
    private Integer screenId;
    private char rowChar;
    private int seatNum;
    private SeatType tier;

    public Seat() {
    }

    public Seat(Integer seatId, Integer screenId, char rowChar, int seatNum, SeatType tier) {
        this.seatId = seatId;
        this.screenId = screenId;
        this.rowChar = Character.toUpperCase(rowChar);
        this.seatNum = seatNum;
        this.tier = tier != null ? tier : SeatType.forRow(rowChar);
    }

    @Override
    public String getId() {
        return seatId != null ? String.valueOf(seatId) : null;
    }

    @Override
    public String getName() {
        return getSeatCode();
    }

    @Override
    public BigDecimal getPrice() {
        return tier != null ? tier.getBasePrice() : SeatType.forRow(rowChar).getBasePrice();
    }

    public String getSeatCode() {
        return String.valueOf(rowChar) + seatNum;
    }

    public Integer getSeatId() {
        return seatId;
    }

    public void setSeatId(Integer seatId) {
        this.seatId = seatId;
    }

    public Integer getScreenId() {
        return screenId;
    }

    public void setScreenId(Integer screenId) {
        this.screenId = screenId;
    }

    public char getRowChar() {
        return rowChar;
    }

    public void setRowChar(char rowChar) {
        this.rowChar = Character.toUpperCase(rowChar);
        if (this.tier == null) {
            this.tier = SeatType.forRow(this.rowChar);
        }
    }

    public int getSeatNum() {
        return seatNum;
    }

    public void setSeatNum(int seatNum) {
        this.seatNum = seatNum;
    }

    public SeatType getTier() {
        return tier;
    }

    public void setTier(SeatType tier) {
        this.tier = tier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Seat other)) return false;
        return rowChar == other.rowChar && seatNum == other.seatNum && Objects.equals(screenId, other.screenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(screenId, rowChar, seatNum);
    }

    @Override
    public String toString() {
        return "Seat{" + getSeatCode() + " (" + tier + ")}";
    }
}

