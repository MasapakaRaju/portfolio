package com.portfolio.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "admin_auth_attempts", indexes = @Index(name = "idx_admin_attempt_requester_created", columnList = "requester_hash, created_at"))
@Getter
@Setter
@NoArgsConstructor
public class AdminAuthAttempt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "requester_hash", nullable = false, length = 64)
    private String requesterHash;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
