package com.portfolio.service;

import com.portfolio.model.AdminOtpChallenge;
import com.portfolio.repository.AdminAccountRepository;
import com.portfolio.repository.AdminOtpChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class AdminOtpAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private final AdminOtpChallengeRepository challenges;
    private final AdminAccountRepository accounts;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean verifyAndConsume(String challengeId, String submittedHash) {
        AdminOtpChallenge challenge = challenges.findByIdForUpdate(challengeId).orElse(null);
        Instant now = Instant.now();
        if (challenge == null || challenge.getConsumedAt() != null
                || !challenge.getExpiresAt().isAfter(now) || challenge.getAttempts() >= MAX_ATTEMPTS) {
            return false;
        }
        challenge.setAttempts(challenge.getAttempts() + 1);
        boolean matches = AdminCrypto.constantTimeEquals(challenge.getOtpHash(), submittedHash)
                && accounts.findByUsernameHashAndActiveTrue(challenge.getUsernameHash()).isPresent();
        if (matches) {
            challenge.setConsumedAt(Instant.now());
        }
        challenges.saveAndFlush(challenge);
        return matches;
    }
}
