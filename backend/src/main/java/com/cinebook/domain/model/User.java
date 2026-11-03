package com.cinebook.domain.model;

import java.time.LocalDateTime;

/**
 * OOP Entity: User.
 */
public class User {

    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private UserRole role = UserRole.CUSTOMER;
    private LocalDateTime createdAt;

    public User() {
    }

    public User(Integer userId, String fullName, String email, String phone, UserRole role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.role = role != null ? role : UserRole.CUSTOMER;
        this.createdAt = LocalDateTime.now();
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

