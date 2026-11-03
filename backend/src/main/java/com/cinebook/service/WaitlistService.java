package com.cinebook.service;

import com.cinebook.dao.ShowDao;
import com.cinebook.dao.UserDao;
import com.cinebook.dao.WaitlistDao;
import com.cinebook.domain.model.User;
import com.cinebook.domain.model.WaitlistEntry;
import com.cinebook.domain.model.WaitlistStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WaitlistService {

    private final WaitlistDao waitlistDao;
    private final UserDao userDao;
    private final ShowDao showDao;

    @Autowired
    public WaitlistService(WaitlistDao waitlistDao, UserDao userDao, ShowDao showDao) {
        this.waitlistDao = waitlistDao;
        this.userDao = userDao;
        this.showDao = showDao;
    }

    @Transactional
    public WaitlistEntry joinWaitlist(int showId, String fullName, String email, String phone, int seatsRequested, String preferredTier) {
        showDao.findById(showId).orElseThrow(() -> new IllegalArgumentException("Show not found: " + showId));

        User customer = userDao.getOrCreateCustomer(fullName, email, phone);

        Optional<WaitlistEntry> existing = waitlistDao.findByUserAndShow(customer.getUserId(), showId);
        if (existing.isPresent() && existing.get().getStatus() == WaitlistStatus.WAITING) {
            return existing.get();
        }

        WaitlistEntry entry = new WaitlistEntry(showId, customer.getUserId(), seatsRequested, preferredTier);
        int waitlistId = waitlistDao.insertWaitlist(entry);
        entry.setWaitlistId(waitlistId);
        entry.setCustomerName(customer.getFullName());
        entry.setCustomerEmail(customer.getEmail());
        return entry;
    }

    @Transactional
    public Optional<WaitlistEntry> promoteNextWaitlistEntry(int showId) {
        Optional<WaitlistEntry> waitingOpt = waitlistDao.findFirstWaiting(showId);
        if (waitingOpt.isPresent()) {
            WaitlistEntry entry = waitingOpt.get();
            entry.setStatus(WaitlistStatus.PROMOTED);
            entry.setPromotedAt(LocalDateTime.now());
            waitlistDao.updateStatus(entry.getWaitlistId(), WaitlistStatus.PROMOTED, entry.getPromotedAt());
            return Optional.of(entry);
        }
        return Optional.empty();
    }

    public List<WaitlistEntry> getWaitlistForShow(int showId) {
        return waitlistDao.findByShowId(showId);
    }
}

