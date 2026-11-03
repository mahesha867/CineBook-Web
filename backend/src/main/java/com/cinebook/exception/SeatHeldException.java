package com.cinebook.exception;

/**
 * Custom domain exception thrown when a seat is currently reserved under an active 5-minute hold by another user.
 */
public class SeatHeldException extends RuntimeException {
    public SeatHeldException(String message) {
        super(message);
    }
}

