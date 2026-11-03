package com.cinebook.domain.model;

import java.math.BigDecimal;

/**
 * OOP Abstraction: Bookable Interface.
 * 
 * Defines the contract for any inventory item in the cinema ecosystem
 * that possesses an identity, a display name, and a monetary price.
 * 
 * Both {@link Seat} and {@link Snack} implement this interface, enabling
 * polymorphic treatment across cart processing and price aggregation.
 */
public interface Bookable {

    /**
     * Unique identifier of the bookable entity (e.g. seatId or snackId).
     */
    String getId();

    /**
     * Human-readable label for tickets, bills, and receipts.
     */
    String getName();

    /**
     * Monetary base price of this item before dynamic adjustments or discounts.
     */
    BigDecimal getPrice();
}

