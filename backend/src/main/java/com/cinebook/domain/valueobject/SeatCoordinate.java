package com.cinebook.domain.valueobject;

import java.util.Objects;

/**
 * OOP Value Object: SeatCoordinate.
 * 
 * Immutable representation of a 2D seating grid position (row and number).
 */
public final class SeatCoordinate implements Comparable<SeatCoordinate> {

    private final char rowChar;
    private final int seatNum;

    public SeatCoordinate(char rowChar, int seatNum) {
        if (seatNum <= 0) {
            throw new IllegalArgumentException("Seat number must be positive: " + seatNum);
        }
        this.rowChar = Character.toUpperCase(rowChar);
        this.seatNum = seatNum;
    }

    public char getRowChar() {
        return rowChar;
    }

    public int getSeatNum() {
        return seatNum;
    }

    public String getCode() {
        return String.valueOf(rowChar) + seatNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SeatCoordinate that)) return false;
        return rowChar == that.rowChar && seatNum == that.seatNum;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rowChar, seatNum);
    }

    @Override
    public int compareTo(SeatCoordinate o) {
        if (this.rowChar != o.rowChar) {
            return Character.compare(this.rowChar, o.rowChar);
        }
        return Integer.compare(this.seatNum, o.seatNum);
    }

    @Override
    public String toString() {
        return getCode();
    }
}

