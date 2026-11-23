package com.portfolio.repository;

import com.portfolio.model.AdminSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AdminSessionRepository extends JpaRepository<AdminSession, Long> {
    Optional<AdminSession> findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(String tokenHash, Instant now);
    void deleteByExpiresAtBefore(Instant before);
}
