package com.portfolio.service;

import com.portfolio.model.ContactMessage;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class SlackServiceTest {

    @Test
    void sendContactNotification_returnsFalseWhenWebhookNotConfigured() throws Exception {
        SlackService service = new SlackService();
        setField(service, "webhookUrl", "");

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Hello");

        boolean result = service.sendContactNotification(msg);
        assertFalse(result);
    }

    @Test
    void sendContactNotification_returnsFalseWhenWebhookIsNull() throws Exception {
        SlackService service = new SlackService();
        setField(service, "webhookUrl", null);

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Hello");

        boolean result = service.sendContactNotification(msg);
        assertFalse(result);
    }

    @Test
    void buildPayload_containsAllContactDetails() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("John Doe");
        msg.setEmail("john@example.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Project Inquiry");
        msg.setMessage("I want to build a website");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("John Doe"));
        assertTrue(payload.contains("john@example.com"));
        assertTrue(payload.contains("+919876543210"));
        assertTrue(payload.contains("Project Inquiry"));
        assertTrue(payload.contains("I want to build a website"));
        assertTrue(payload.contains("New Portfolio Enquiry"));
    }

    @Test
    void buildPayload_escapesSpecialCharacters() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("Test \"User\"");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi\nThere");
        msg.setMessage("Message with \"quotes\" and \\backslash");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("Test \\\"User\\\""));
        assertTrue(payload.contains("Hi\\nThere"));
        assertTrue(payload.contains("Message with \\\"quotes\\\" and \\\\backslash"));
    }

    @Test
    void buildPayload_handlesNullFields() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName(null);
        msg.setEmail(null);
        msg.setPhone(null);
        msg.setSubject(null);
        msg.setMessage(null);

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertNotNull(payload);
        assertTrue(payload.contains("New Portfolio Enquiry"));
    }

    @Test
    void sendContactNotification_returnsFalseWhenWebhookIsBlank() throws Exception {
        SlackService service = new SlackService();
        setField(service, "webhookUrl", "   ");

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Hello");

        boolean result = service.sendContactNotification(msg);
        assertFalse(result);
    }

    @Test
    void buildPayload_containsSlackBlocksStructure() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Hello");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("\"blocks\""));
        assertTrue(payload.contains("\"type\": \"header\""));
        assertTrue(payload.contains("\"type\": \"section\""));
        assertTrue(payload.contains("\"type\": \"divider\""));
        assertTrue(payload.contains("\"type\": \"mrkdwn\""));
    }

    @Test
    void buildPayload_escapesCarriageReturn() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Line1\r\nLine2");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("Line1\\r\\nLine2"));
    }

    @Test
    void buildPayload_escapesTab() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("Test");
        msg.setEmail("test@test.com");
        msg.setPhone("+919876543210");
        msg.setSubject("Hi");
        msg.setMessage("Col1\tCol2");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("Col1\\tCol2"));
    }

    @Test
    void buildPayload_usesFormattedTemplate() throws Exception {
        SlackService service = new SlackService();

        ContactMessage msg = new ContactMessage();
        msg.setName("Alice");
        msg.setEmail("alice@test.com");
        msg.setPhone("+447911123456");
        msg.setSubject("Meeting");
        msg.setMessage("Hello");

        Method buildPayload = SlackService.class.getDeclaredMethod("buildPayload", ContactMessage.class);
        buildPayload.setAccessible(true);
        String payload = (String) buildPayload.invoke(service, msg);

        assertTrue(payload.contains("*Name:* Alice"));
        assertTrue(payload.contains("*Email:* alice@test.com"));
        assertTrue(payload.contains("*Phone:* +447911123456"));
        assertTrue(payload.contains("*Subject:* Meeting"));
        assertTrue(payload.contains("*Message:*"));
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
