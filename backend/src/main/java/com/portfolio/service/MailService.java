package com.portfolio.service;

import com.portfolio.model.ContactMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    @Value("${spring.mail.password:}")
    private String mailPassword;

    @Value("${app.mail.from:}")
    private String mailFrom;

    @Value("${app.mail.recipient}")
    private String recipient;

    public void sendVerification(String email, String name, String code) {
        send(email, "Verify your portfolio enquiry", """
                Hi %s,

                Your verification code is: %s

                Enter this code on Masapaka Raju's portfolio to send your message. It expires in 15 minutes.
                """.formatted(name, code));
    }

    public void sendContactNotification(ContactMessage contact) {
        send(recipient, "New portfolio enquiry: " + contact.getSubject(), """
                You received a new portfolio enquiry.

                Name: %s
                Email: %s
                Mobile: %s
                Subject: %s

                Message:
                %s
                """.formatted(contact.getName(), contact.getEmail(), contact.getPhone(), contact.getSubject(), contact.getMessage()));
    }

    private void send(String destination, String subject, String text) {
        if (mailUsername.isBlank() || mailPassword.isBlank()) {
            throw new IllegalStateException("Email delivery is not configured");
        }

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(mailFrom.isBlank() ? mailUsername : mailFrom);
        mail.setTo(destination);
        mail.setSubject(subject);
        mail.setText(text);
        mailSender.send(mail);
    }
}
