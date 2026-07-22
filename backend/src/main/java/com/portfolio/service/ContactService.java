package com.portfolio.service;

import com.portfolio.dto.ContactRequest;
import com.portfolio.dto.VerifyContactRequest;
import com.portfolio.model.ContactVerification;
import com.portfolio.model.ContactMessage;
import com.portfolio.repository.ContactVerificationRepository;
import com.portfolio.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;
import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final ContactVerificationRepository contactVerificationRepository;
    private final MailService mailService;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Transactional
    public void requestVerification(ContactRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        contactVerificationRepository.deleteByEmailAndUsedFalse(email);

        ContactVerification verification = new ContactVerification();
        verification.setName(request.getName().trim());
        verification.setEmail(email);
        verification.setPhone(request.getPhone());
        verification.setSubject(request.getSubject().trim());
        verification.setMessage(request.getMessage().trim());
        verification.setCode(String.format("%06d", RANDOM.nextInt(1_000_000)));
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verification.setUsed(false);
        contactVerificationRepository.save(verification);

        mailService.sendVerification(email, verification.getName(), verification.getCode());
    }

    @Transactional
    public ContactMessage verifyAndSave(VerifyContactRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        ContactVerification verification = contactVerificationRepository
                .findTopByEmailAndCodeAndUsedFalseOrderByCreatedAtDesc(email, request.getCode())
                .orElseThrow(() -> new IllegalArgumentException("Enter the valid verification code sent to your email"));

        if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("This verification code has expired. Please submit the form again.");
        }

        verification.setUsed(true);
        contactVerificationRepository.save(verification);

        ContactMessage message = new ContactMessage();
        message.setName(verification.getName());
        message.setEmail(verification.getEmail());
        message.setPhone(verification.getPhone());
        message.setSubject(verification.getSubject());
        message.setMessage(verification.getMessage());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);
        ContactMessage savedMessage = contactMessageRepository.save(message);
        mailService.sendContactNotification(savedMessage);
        return savedMessage;
    }
}
