package com.cinebook.dao;

import com.cinebook.domain.model.User;

import java.util.Optional;

public interface UserDao {
    Optional<User> findById(int userId);
    Optional<User> findByEmail(String email);
    Optional<User> findByPhone(String phone);
    User getOrCreateCustomer(String fullName, String email, String phone);
}

