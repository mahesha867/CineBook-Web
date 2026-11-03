package com.cinebook.domain.model;

import java.math.BigDecimal;

/**
 * OOP Enum: SeatType.
 * 
 * Encapsulates the cinema seating tiers and their authoritative base pricing.
 * Academic Rule: Seat tier pricing resides strictly within Java code,
 * avoiding hardcoded price columns inside database seat inventory tables.
 * 
 * Rows A-B: REGULAR (Rs. 150.00)
 * Rows C-D: PREMIUM (Rs. 250.00)
 * Row E:    VIP     (Rs. 400.00)
 */
public enum SeatType {
    REGULAR(new BigDecimal("150.00"), "Regular Comfort"),
    PREMIUM(new BigDecimal("250.00"), "Premium Prime View"),
    VIP(new BigDecimal("400.00"), "VIP Recliner Experience");

    private final BigDecimal basePrice;
    private final String description;

    SeatType(BigDecimal basePrice, String description) {
        this.basePrice = basePrice;
        this.description = description;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Determines the SeatType based on row character.
     * 
     * @param rowChar row letter ('A' to 'E')
     * @return matching SeatType
     */
    public static SeatType forRow(char rowChar) {
        char upper = Character.toUpperCase(rowChar);
        return switch (upper) {
            case 'A', 'B' -> REGULAR;
            case 'C', 'D' -> PREMIUM;
            case 'E' -> VIP;
            default -> throw new IllegalArgumentException("Invalid cinema row: " + rowChar + ". Allowed: A-E.");
        };
    }
}

