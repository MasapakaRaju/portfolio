package com.portfolio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "admin_otp_challenges", indexes = {
        @Index(name = "idx_admin_challenge_requester_created", columnList = "requester_hash, created_at"),
        @Index(name = "idx_admin_challenge_expiry", columnList = "expires_at")
})
@Getter
@Setter
@NoArgsConstructor
public class AdminOtpChallenge {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "username_hash", nullable = false, length = 64)
    private String usernameHash;

    @Column(name = "otp_hash", nullable = false, length = 64)
    private String otpHash;

    @Column(name = "requester_hash", nullable = false, length = 64)
    private String requesterHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private int attempts;

    @Column(name = "consumed_at")
    private Instant consumedAt;
}
