package com.cinebook.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class JoinWaitlistRequest {

    @NotNull(message = "showId is required")
    private Integer showId;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer email is required")
    @Email(message = "Valid email is required")
    private String customerEmail;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    @Min(value = 1, message = "At least 1 seat must be requested")
    private int seatsRequested = 1;

    private String preferredTier = "ANY";

    public Integer getShowId() {
        return showId;
    }

    public void setShowId(Integer showId) {
        this.showId = showId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getSeatsRequested() {
        return seatsRequested;
    }

    public void setSeatsRequested(int seatsRequested) {
        this.seatsRequested = seatsRequested;
    }

    public String getPreferredTier() {
        return preferredTier;
    }

    public void setPreferredTier(String preferredTier) {
        this.preferredTier = preferredTier;
    }
}

