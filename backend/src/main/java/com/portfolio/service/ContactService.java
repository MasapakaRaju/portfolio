package com.portfolio.service;

import com.portfolio.dto.ContactRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.repository.ContactMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMessageRepository contactMessageRepository;
    private final SlackService slackService;

    @Transactional
    public ContactMessage saveAndNotify(ContactRequest request) {
        ContactMessage message = new ContactMessage();
        message.setName(request.getName().trim());
        message.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        message.setPhone(request.getPhone());
        message.setSubject(request.getSubject().trim());
        message.setMessage(request.getMessage().trim());
        message.setSentAt(LocalDateTime.now());
        message.setIsRead(false);

        ContactMessage saved = contactMessageRepository.save(message);
        slackService.sendContactNotification(saved);
        return saved;
    }
}
