package com.portfolio.service;

import com.portfolio.model.ContactMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Slf4j
@Service
public class SlackService {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    @Value("${app.slack.webhook-url:}")
    private String webhookUrl;

    public boolean sendContactNotification(ContactMessage contact) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            log.warn("Slack webhook URL is not configured — skipping notification");
            return false;
        }

        String payload = buildPayload(contact);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .timeout(Duration.ofSeconds(10))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                log.info("Slack notification sent for contact from {}", contact.getEmail());
                return true;
            } else {
                log.error("Slack returned status {} body: {}", response.statusCode(), response.body());
                return false;
            }
        } catch (Exception e) {
            log.error("Failed to send Slack notification", e);
            return false;
        }
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    private String buildPayload(ContactMessage contact) {
        String name = escapeJson(contact.getName());
        String email = escapeJson(contact.getEmail());
        String phone = escapeJson(contact.getPhone());
        String subject = escapeJson(contact.getSubject());
        String message = escapeJson(contact.getMessage());

        return """
                {
                  "blocks": [
                    {
                      "type": "header",
                      "text": {
                        "type": "plain_text",
                        "text": "New Portfolio Enquiry",
                        "emoji": true
                      }
                    },
                    {
                      "type": "section",
                      "fields": [
                        { "type": "mrkdwn", "text": "*Name:* %s" },
                        { "type": "mrkdwn", "text": "*Email:* %s" },
                        { "type": "mrkdwn", "text": "*Phone:* %s" },
                        { "type": "mrkdwn", "text": "*Subject:* %s" }
                      ]
                    },
                    {
                      "type": "section",
                      "text": {
                        "type": "mrkdwn",
                        "text": "*Message:*\\n%s"
                      }
                    },
                    {
                      "type": "divider"
                    }
                  ]
                }
                """.formatted(name, email, phone, subject, message);
    }
}
