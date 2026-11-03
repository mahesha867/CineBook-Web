package com.cinebook.exception;

/**
 * Custom domain exception thrown when all seats for a show are occupied.
 */
public class ShowFullException extends RuntimeException {
    public ShowFullException(String message) {
        super(message);
    }
}

