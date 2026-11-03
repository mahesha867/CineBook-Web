package com.cinebook.exception;

/**
 * Custom domain exception thrown when a coupon code is unrecognized, expired, or inactive.
 */
public class InvalidCouponException extends RuntimeException {
    public InvalidCouponException(String message) {
        super(message);
    }
}

