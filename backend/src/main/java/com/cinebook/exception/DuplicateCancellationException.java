package com.cinebook.exception;

/**
 * Custom domain exception thrown when attempting to cancel an already cancelled booking.
 */
public class DuplicateCancellationException extends RuntimeException {
    public DuplicateCancellationException(String message) {
        super(message);
    }
}

