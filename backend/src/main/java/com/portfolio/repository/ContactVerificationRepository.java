package com.portfolio.repository;

import com.portfolio.model.ContactVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ContactVerificationRepository extends JpaRepository<ContactVerification, Long> {

    void deleteByEmailAndUsedFalse(String email);

    Optional<ContactVerification> findTopByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(String email, String code);
}
