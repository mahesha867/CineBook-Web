package com.cinebook.dao;

import com.cinebook.domain.model.WaitlistEntry;
import com.cinebook.domain.model.WaitlistStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WaitlistDao {
    int insertWaitlist(WaitlistEntry entry);
    Optional<WaitlistEntry> findFirstWaiting(int showId);
    void updateStatus(int waitlistId, WaitlistStatus status, LocalDateTime promotedAt);
    List<WaitlistEntry> findByShowId(int showId);
    Optional<WaitlistEntry> findByUserAndShow(int userId, int showId);
}

