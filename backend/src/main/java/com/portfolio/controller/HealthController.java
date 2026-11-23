package com.portfolio.controller;

import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HealthController {

    private final String commit;

    public HealthController(Environment environment) {
        String imageCommit = environment.getProperty("APP_COMMIT");
        String renderCommit = environment.getProperty("RENDER_GIT_COMMIT");
        this.commit = StringUtils.hasText(imageCommit)
                ? imageCommit
                : StringUtils.hasText(renderCommit) ? renderCommit : "local";
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "ok", "commit", commit));
    }
}
