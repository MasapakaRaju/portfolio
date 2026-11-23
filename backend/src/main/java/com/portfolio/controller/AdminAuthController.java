package com.portfolio.controller;

import com.portfolio.dto.AdminChallengeRequest;
import com.portfolio.dto.AdminOtpVerifyRequest;
import com.portfolio.service.AdminAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {
    private final AdminAuthService adminAuthService;

    @PostMapping("/challenge")
    public ResponseEntity<Map<String, Object>> requestOtp(
            @Valid @RequestBody AdminChallengeRequest request,
            HttpServletRequest servletRequest) {
        AdminAuthService.ChallengeResult result = adminAuthService.requestOtp(
                request.getAdminName(), requesterAddress(servletRequest));
        return ResponseEntity.status(HttpStatus.ACCEPTED).cacheControl(CacheControl.noStore()).body(Map.of(
                "challengeId", result.challengeId(),
                "expiresInSeconds", result.expiresInSeconds(),
                "message", "If the admin account is configured, a verification code has been sent to Slack."
        ));
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, Object>> verifyOtp(
            @Valid @RequestBody AdminOtpVerifyRequest request,
            HttpServletRequest servletRequest) {
        AdminAuthService.SessionResult result = adminAuthService.verifyOtp(
                request.getChallengeId(), request.getOtp(), requesterAddress(servletRequest));
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(Map.of("token", result.token(), "expiresInSeconds", result.expiresInSeconds()));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, String>> session(@RequestHeader(name = "Authorization", required = false) String authorization) {
        String token = bearerToken(authorization);
        if (!adminAuthService.isValidSession(token)) {
            throw new org.springframework.web.server.ResponseStatusException(HttpStatus.UNAUTHORIZED, "Admin session expired.");
        }
        return ResponseEntity.ok().cacheControl(CacheControl.noStore())
                .body(Map.of("name", "Admin", "message", "Welcome, Admin."));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader(name = "Authorization", required = false) String authorization) {
        adminAuthService.logout(bearerToken(authorization));
        return ResponseEntity.noContent().build();
    }

    private String bearerToken(String authorization) {
        if (authorization == null || !authorization.regionMatches(true, 0, "Bearer ", 0, 7)) return null;
        return authorization.substring(7).trim();
    }

    private String requesterAddress(HttpServletRequest request) {
        // Render's Cloudflare edge overwrites CF-Connecting-IP with the visitor address.
        String cloudflareAddress = request.getHeader("CF-Connecting-IP");
        if (cloudflareAddress != null && cloudflareAddress.matches("[0-9a-fA-F:.]{3,45}")) {
            return cloudflareAddress;
        }
        return request.getRemoteAddr();
    }
}
