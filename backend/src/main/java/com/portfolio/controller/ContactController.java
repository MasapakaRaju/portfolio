package com.portfolio.controller;

import com.portfolio.dto.ContactRequest;
import com.portfolio.dto.VerifyContactRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<Map<String, String>> sendMessage(@Valid @RequestBody ContactRequest request) {
        contactService.requestVerification(request);
        return ResponseEntity.ok(Map.of("message", "We sent a verification code to your email. Enter it below to send your message."));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyAndSend(@Valid @RequestBody VerifyContactRequest request) {
        contactService.verifyAndSave(request);
        return ResponseEntity.ok(Map.of("message", "Your message has been sent. I'll get back to you soon!"));
    }
}
