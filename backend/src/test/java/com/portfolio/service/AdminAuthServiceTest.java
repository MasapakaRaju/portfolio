package com.portfolio.service;

import com.portfolio.model.AdminAccount;
import com.portfolio.model.AdminOtpChallenge;
import com.portfolio.model.AdminSession;
import com.portfolio.repository.AdminAccountRepository;
import com.portfolio.repository.AdminOtpChallengeRepository;
import com.portfolio.repository.AdminSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthServiceTest {
    private static final String OTP_SECRET = "a-test-only-secret-with-at-least-32-chars";

    @Mock private AdminAccountRepository accounts;
    @Mock private AdminOtpChallengeRepository challenges;
    @Mock private AdminSessionRepository sessions;
    @Mock private AdminRateLimitService rateLimit;
    @Mock private AdminOtpAttemptService otpAttempts;
    @Mock private AdminOtpNotifier notifier;

    private AdminAuthService service;

    @BeforeEach
    void setUp() {
        service = new AdminAuthService(accounts, challenges, sessions, rateLimit, otpAttempts, notifier);
        ReflectionTestUtils.setField(service, "otpHmacSecret", OTP_SECRET);
        when(accounts.findFirstByActiveTrue()).thenReturn(Optional.of(new AdminAccount()));
    }

    @Test
    void storesOnlyHmacOfRandomOtpAndSendsItThroughNotifier() {
        String adminName = "Admin@Raju";
        String usernameHash = AdminCrypto.sha256(adminName);
        when(accounts.findByUsernameHashAndActiveTrue(usernameHash)).thenReturn(Optional.of(new AdminAccount()));
        when(challenges.findAllByUsernameHashAndConsumedAtIsNullAndExpiresAtAfter(eq(usernameHash), any(Instant.class)))
                .thenReturn(List.of());
        when(rateLimit.requesterHash("127.0.0.1")).thenReturn("requester-hash");
        when(notifier.send(anyString())).thenReturn(true);

        AdminAuthService.ChallengeResult result = service.requestOtp(adminName, "127.0.0.1");

        ArgumentCaptor<AdminOtpChallenge> captor = ArgumentCaptor.forClass(AdminOtpChallenge.class);
        verify(challenges).save(captor.capture());
        AdminOtpChallenge savedChallenge = captor.getValue();
        ArgumentCaptor<String> otpCaptor = ArgumentCaptor.forClass(String.class);
        verify(notifier).send(otpCaptor.capture());
        String expectedHash = AdminCrypto.hmacSha256(OTP_SECRET,
                "admin-otp:" + usernameHash + ":" + result.challengeId() + ":" + otpCaptor.getValue());

        assertEquals(6, otpCaptor.getValue().length());
        assertEquals(usernameHash, savedChallenge.getUsernameHash());
        assertEquals(expectedHash, savedChallenge.getOtpHash());
        assertNotEquals(otpCaptor.getValue(), savedChallenge.getOtpHash());
        assertEquals(300, result.expiresInSeconds());
    }

    @Test
    void successfulOtpCreatesOnlyHashedOpaqueSession() {
        String usernameHash = AdminCrypto.sha256("Admin@Raju");
        AdminOtpChallenge challenge = new AdminOtpChallenge();
        challenge.setId("c0aa2d22-9f6e-4ba1-8b52-38f3d18de131");
        challenge.setUsernameHash(usernameHash);
        challenge.setOtpHash("stored-hmac");
        when(challenges.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        when(otpAttempts.verifyAndConsume(eq(challenge.getId()), anyString())).thenReturn(true);

        AdminAuthService.SessionResult result = service.verifyOtp(challenge.getId(), "123456", "127.0.0.1");

        ArgumentCaptor<AdminSession> captor = ArgumentCaptor.forClass(AdminSession.class);
        verify(sessions).save(captor.capture());
        assertEquals(1800, result.expiresInSeconds());
        assertNotEquals(result.token(), captor.getValue().getTokenHash());
        assertEquals(AdminCrypto.sha256(result.token()), captor.getValue().getTokenHash());
        assertEquals(usernameHash, captor.getValue().getUsernameHash());
    }
}
