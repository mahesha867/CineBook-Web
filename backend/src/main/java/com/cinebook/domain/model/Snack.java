package com.cinebook.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * OOP Entity: Snack.
 * 
 * Implements {@link Bookable}, demonstrating polymorphism alongside {@link Seat}.
 * Concession snacks come dynamically from the database.
 */
public class Snack implements Bookable {

    private String snackId;
    private String name;
    private BigDecimal price;
    private SnackCategory category;
    private boolean isAvailable;

    public Snack() {
    }

    public Snack(String snackId, String name, BigDecimal price, SnackCategory category, boolean isAvailable) {
        this.snackId = snackId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.isAvailable = isAvailable;
    }

    @Override
    public String getId() {
        return snackId;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public BigDecimal getPrice() {
        return price;
    }

    public String getSnackId() {
        return snackId;
    }

    public void setSnackId(String snackId) {
        this.snackId = snackId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public SnackCategory getCategory() {
        return category;
    }

    public void setCategory(SnackCategory category) {
        this.category = category;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Snack other)) return false;
        return Objects.equals(snackId, other.snackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(snackId);
    }

    @Override
    public String toString() {
        return "Snack{" + snackId + ": " + name + " (Rs. " + price + ")}";
    }
}

