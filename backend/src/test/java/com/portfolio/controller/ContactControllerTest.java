package com.portfolio.controller;

import com.portfolio.dto.ContactRequest;
import com.portfolio.model.ContactMessage;
import com.portfolio.service.ContactService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ContactController.class)
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactService contactService;

    @Autowired
    private ObjectMapper objectMapper;

    private ContactRequest validRequest() {
        ContactRequest req = new ContactRequest();
        req.setName("John Doe");
        req.setEmail("john@example.com");
        req.setPhone("+919876543210");
        req.setSubject("Hello");
        req.setMessage("Test message");
        return req;
    }

    private ContactMessage savedMessage() {
        ContactMessage msg = new ContactMessage();
        msg.setId(1L);
        msg.setName("John Doe");
        msg.setEmail("john@example.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hello");
        msg.setMessage("Test message");
        msg.setSentAt(LocalDateTime.now());
        msg.setIsRead(false);
        return msg;
    }

    @Test
    void sendMessage_validRequest_returns200() throws Exception {
        when(contactService.saveAndNotify(any())).thenReturn(savedMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Your message has been sent successfully. I'll get back to you soon!"));
    }

    @Test
    void sendMessage_missingName_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setName("");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is required"));
    }

    @Test
    void sendMessage_invalidEmail_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setEmail("not-an-email");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    void sendMessage_invalidPhone_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("123");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enter a valid mobile number"));
    }

    @Test
    void sendMessage_missingSubject_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setSubject("");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subject is required"));
    }

    @Test
    void sendMessage_missingMessage_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setMessage("");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message is required"));
    }

    @Test
    void sendMessage_emptyBody_returns400() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sendMessage_validPhoneWithPlus_returns200() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+14155552671");

        when(contactService.saveAndNotify(any())).thenReturn(savedMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage_validPhoneWithoutPlus_returns200() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("919876543210");

        when(contactService.saveAndNotify(any())).thenReturn(savedMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage_phoneStartsWithZero_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("0987654321");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enter a valid mobile number"));
    }

    @Test
    void sendMessage_phoneWithLetters_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+91abc12345");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enter a valid mobile number"));
    }

    @Test
    void sendMessage_phoneWithSpaces_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+91 9876543210");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enter a valid mobile number"));
    }

    @Test
    void sendMessage_phoneTooShort_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+91123");

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enter a valid mobile number"));
    }

    @Test
    void sendMessage_nameTooLong_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setName("A".repeat(101));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Name is too long"));
    }

    @Test
    void sendMessage_subjectTooLong_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setSubject("A".repeat(181));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Subject is too long"));
    }

    @Test
    void sendMessage_messageTooLong_returns400() throws Exception {
        ContactRequest req = validRequest();
        req.setMessage("A".repeat(5001));

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Message is too long"));
    }

    @Test
    void sendMessage_nullBody_returns500() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    @Test
    void sendMessage_invalidContentType_returns500() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("not json"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    @Test
    void sendMessage_malformedJson_returns500() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{invalid json}"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Something went wrong. Please try again later."));
    }

    @Test
    void sendMessage_validMinLengthPhone_returns200() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+9112345678");

        when(contactService.saveAndNotify(any())).thenReturn(savedMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void sendMessage_validMaxLengthPhone_returns200() throws Exception {
        ContactRequest req = validRequest();
        req.setPhone("+912345678901234");

        when(contactService.saveAndNotify(any())).thenReturn(savedMessage());

        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
