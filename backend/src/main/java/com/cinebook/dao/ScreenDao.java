package com.cinebook.dao;

import com.cinebook.domain.model.Screen;

import java.util.List;
import java.util.Optional;

public interface ScreenDao {
    Optional<Screen> findById(int screenId);
    List<Screen> findAll();
}

