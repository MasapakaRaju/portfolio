package com.portfolio.service;

import com.portfolio.dto.ContactRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.repository.ContactMessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactMessageRepository contactMessageRepository;

    @Mock
    private SlackService slackService;

    @InjectMocks
    private ContactService contactService;

    @Test
    void saveAndNotify_persistsMessageAndSendsSlackNotification() {
        ContactRequest request = new ContactRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setPhone("+919876543210");
        request.setSubject("Hello");
        request.setMessage("Test message");

        when(contactMessageRepository.save(any(ContactMessage.class))).thenAnswer(inv -> {
            ContactMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return msg;
        });
        when(slackService.sendContactNotification(any())).thenReturn(true);

        ContactMessage saved = contactService.saveAndNotify(request);

        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(contactMessageRepository).save(captor.capture());
        verify(slackService).sendContactNotification(captor.capture());

        ContactMessage captured = captor.getValue();
        assertEquals("John Doe", captured.getName());
        assertEquals("john@example.com", captured.getEmail());
        assertEquals("+919876543210", captured.getPhone());
        assertEquals("Hello", captured.getSubject());
        assertEquals("Test message", captured.getMessage());
        assertNotNull(captured.getSentAt());
        assertFalse(captured.getIsRead());
    }

    @Test
    void saveAndNotify_normalizesEmailToLowercase() {
        ContactRequest request = new ContactRequest();
        request.setName("Jane");
        request.setEmail("JANE@Example.COM");
        request.setPhone("+11234567890");
        request.setSubject("Hi");
        request.setMessage("Body");

        when(contactMessageRepository.save(any())).thenAnswer(inv -> {
            ContactMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return msg;
        });
        when(slackService.sendContactNotification(any())).thenReturn(true);

        contactService.saveAndNotify(request);

        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(contactMessageRepository).save(captor.capture());
        assertEquals("jane@example.com", captor.getValue().getEmail());
    }

    @Test
    void saveAndNotify_trimsWhitespace() {
        ContactRequest request = new ContactRequest();
        request.setName("  Alice  ");
        request.setEmail("  alice@test.com  ");
        request.setPhone("+447911123456");
        request.setSubject("  Meeting  ");
        request.setMessage("  Hello world  ");

        when(contactMessageRepository.save(any())).thenAnswer(inv -> {
            ContactMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return msg;
        });
        when(slackService.sendContactNotification(any())).thenReturn(true);

        contactService.saveAndNotify(request);

        ArgumentCaptor<ContactMessage> captor = ArgumentCaptor.forClass(ContactMessage.class);
        verify(contactMessageRepository).save(captor.capture());
        ContactMessage msg = captor.getValue();
        assertEquals("Alice", msg.getName());
        assertEquals("alice@test.com", msg.getEmail());
        assertEquals("Meeting", msg.getSubject());
        assertEquals("Hello world", msg.getMessage());
    }

    @Test
    void saveAndNotify_stillSavesWhenSlackFails() {
        ContactRequest request = new ContactRequest();
        request.setName("Bob");
        request.setEmail("bob@test.com");
        request.setPhone("+919876543210");
        request.setSubject("Test");
        request.setMessage("msg");

        when(contactMessageRepository.save(any())).thenAnswer(inv -> {
            ContactMessage msg = inv.getArgument(0);
            msg.setId(1L);
            return msg;
        });
        when(slackService.sendContactNotification(any())).thenReturn(false);

        ContactMessage saved = contactService.saveAndNotify(request);

        assertNotNull(saved);
        verify(contactMessageRepository).save(any());
        verify(slackService).sendContactNotification(any());
    }
}
