package com.portfolio.service;

import com.portfolio.model.AdminAuthAttempt;
import com.portfolio.repository.AdminAuthAttemptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class AdminRateLimitService {
    private static final int MAX_ATTEMPTS = 8;
    private final AdminAuthAttemptRepository attempts;

    @Value("${app.admin.otp-hmac-secret:}")
    private String hmacSecret;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordAndCheck(String requesterAddress) {
        Instant now = Instant.now();
        String requesterHash = hash(requesterAddress == null ? "unknown" : requesterAddress);
        long recentAttempts = attempts.countByRequesterHashAndCreatedAtAfter(
                requesterHash, now.minus(15, ChronoUnit.MINUTES));

        AdminAuthAttempt attempt = new AdminAuthAttempt();
        attempt.setRequesterHash(requesterHash);
        attempt.setCreatedAt(now);
        attempts.save(attempt);
        attempts.deleteByCreatedAtBefore(now.minus(1, ChronoUnit.DAYS));

        if (recentAttempts >= MAX_ATTEMPTS) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Too many login attempts. Try again later.");
        }
    }

    public String requesterHash(String requesterAddress) {
        return hash(requesterAddress == null ? "unknown" : requesterAddress);
    }

    private String hash(String value) {
        if (hmacSecret == null || hmacSecret.length() < 32) {
            throw new IllegalStateException("Admin OTP secret is not configured");
        }
        return AdminCrypto.hmacSha256(hmacSecret, "rate-limit:" + value);
    }
}
