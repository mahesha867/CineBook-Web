package com.cinebook.exception;

/**
 * Custom domain exception thrown when one or more requested seats are already confirmed booked.
 */
public class SeatUnavailableException extends RuntimeException {
    public SeatUnavailableException(String message) {
        super(message);
    }
}

