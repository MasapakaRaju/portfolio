package com.portfolio.repository;

import com.portfolio.model.AdminOtpChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;

public interface AdminOtpChallengeRepository extends JpaRepository<AdminOtpChallenge, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select challenge from AdminOtpChallenge challenge where challenge.id = :id")
    Optional<AdminOtpChallenge> findByIdForUpdate(@Param("id") String id);

    long countByRequesterHashAndCreatedAtAfter(String requesterHash, Instant after);
    long countByUsernameHashAndCreatedAtAfter(String usernameHash, Instant after);
    List<AdminOtpChallenge> findAllByUsernameHashAndConsumedAtIsNullAndExpiresAtAfter(String usernameHash, Instant now);
    void deleteByCreatedAtBefore(Instant before);
}
