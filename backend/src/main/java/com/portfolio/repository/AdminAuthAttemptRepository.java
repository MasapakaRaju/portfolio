package com.portfolio.repository;

import com.portfolio.model.AdminAuthAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface AdminAuthAttemptRepository extends JpaRepository<AdminAuthAttempt, Long> {
    long countByRequesterHashAndCreatedAtAfter(String requesterHash, Instant after);
    void deleteByCreatedAtBefore(Instant before);
}
