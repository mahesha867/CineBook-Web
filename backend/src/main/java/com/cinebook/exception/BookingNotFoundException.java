package com.cinebook.exception;

/**
 * Custom domain exception thrown when a booking ID cannot be found.
 */
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}

