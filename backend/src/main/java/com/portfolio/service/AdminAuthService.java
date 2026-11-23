package com.portfolio.service;

import com.portfolio.model.AdminAccount;
import com.portfolio.model.AdminOtpChallenge;
import com.portfolio.model.AdminSession;
import com.portfolio.repository.AdminAccountRepository;
import com.portfolio.repository.AdminOtpChallengeRepository;
import com.portfolio.repository.AdminSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdminAuthService {
    private static final long OTP_MINUTES = 5;
    private static final long SESSION_MINUTES = 30;

    private final AdminAccountRepository accounts;
    private final AdminOtpChallengeRepository challenges;
    private final AdminSessionRepository sessions;
    private final AdminRateLimitService rateLimit;
    private final AdminOtpAttemptService otpAttempts;
    private final AdminOtpNotifier notifier;

    @Value("${app.admin.otp-hmac-secret:}")
    private String otpHmacSecret;

    @Transactional
    public ChallengeResult requestOtp(String adminName, String requesterAddress) {
        ensureConfigured();
        rateLimit.recordAndCheck(requesterAddress);

        Instant now = Instant.now();
        challenges.deleteByCreatedAtBefore(now.minus(1, ChronoUnit.DAYS));
        sessions.deleteByExpiresAtBefore(now.minus(1, ChronoUnit.DAYS));
        String usernameHash = AdminCrypto.sha256(adminName);
        boolean accountExists = accounts.findByUsernameHashAndActiveTrue(usernameHash).isPresent();
        long recentChallenges = challenges.countByUsernameHashAndCreatedAtAfter(
                usernameHash, Instant.now().minus(15, ChronoUnit.MINUTES));
        boolean shouldSendOtp = accountExists && recentChallenges < 3;
        String challengeId = UUID.randomUUID().toString();
        String otp = AdminCrypto.randomOtp();
        if (shouldSendOtp) {
            for (AdminOtpChallenge old : challenges.findAllByUsernameHashAndConsumedAtIsNullAndExpiresAtAfter(usernameHash, now)) {
                old.setConsumedAt(now);
            }
        }

        AdminOtpChallenge challenge = new AdminOtpChallenge();
        challenge.setId(challengeId);
        challenge.setUsernameHash(usernameHash);
        challenge.setOtpHash(otpHash(usernameHash, challengeId, otp));
        challenge.setRequesterHash(rateLimit.requesterHash(requesterAddress));
        challenge.setCreatedAt(now);
        challenge.setExpiresAt(now.plus(OTP_MINUTES, ChronoUnit.MINUTES));
        challenges.save(challenge);

        if (shouldSendOtp && !notifier.send(otp)) {
            challenge.setConsumedAt(now);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Admin login is temporarily unavailable. Check the Slack webhook configuration.");
        }
        return new ChallengeResult(challengeId, OTP_MINUTES * 60);
    }

    @Transactional
    public SessionResult verifyOtp(String challengeId, String otp, String requesterAddress) {
        ensureConfigured();
        rateLimit.recordAndCheck(requesterAddress);

        AdminOtpChallenge challenge = challenges.findById(challengeId).orElseThrow(AdminAuthService::invalidCode);
        String submittedHash = otpHash(challenge.getUsernameHash(), challengeId, otp);
        if (!otpAttempts.verifyAndConsume(challengeId, submittedHash)) {
            throw invalidCode();
        }

        Instant now = Instant.now();
        String rawToken = AdminCrypto.randomToken();
        AdminSession session = new AdminSession();
        session.setTokenHash(AdminCrypto.sha256(rawToken));
        session.setUsernameHash(challenge.getUsernameHash());
        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(SESSION_MINUTES, ChronoUnit.MINUTES));
        sessions.save(session);
        return new SessionResult(rawToken, SESSION_MINUTES * 60);
    }

    @Transactional(readOnly = true)
    public boolean isValidSession(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return false;
        return sessions.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(AdminCrypto.sha256(rawToken), Instant.now())
                .flatMap(session -> accounts.findByUsernameHashAndActiveTrue(session.getUsernameHash()))
                .isPresent();
    }

    @Transactional
    public void logout(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        sessions.findByTokenHashAndRevokedAtIsNullAndExpiresAtAfter(AdminCrypto.sha256(rawToken), Instant.now())
                .ifPresent(session -> {
                    session.setRevokedAt(Instant.now());
                    sessions.save(session);
                });
    }

    private String otpHash(String usernameHash, String challengeId, String otp) {
        return AdminCrypto.hmacSha256(otpHmacSecret,
                "admin-otp:" + usernameHash + ":" + challengeId + ":" + otp);
    }

    private void ensureConfigured() {
        if (otpHmacSecret == null || otpHmacSecret.length() < 32
                || accounts.findFirstByActiveTrue().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Admin login is not configured.");
        }
    }

    private static ResponseStatusException invalidCode() {
        return new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                "The verification code is invalid or expired. Request a new code and try again.");
    }

    public record ChallengeResult(String challengeId, long expiresInSeconds) {}
    public record SessionResult(String token, long expiresInSeconds) {}
}
