package com.portfolio.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_verifications")
@Data
public class ContactVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phone;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String message;
    private String code;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean used = false;
}
