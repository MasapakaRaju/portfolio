package com.portfolio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AdminOtpVerifyRequest {
    @NotBlank
    @Size(max = 36)
    @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Invalid verification challenge")
    private String challengeId;

    @NotBlank
    @Pattern(regexp = "^[0-9]{6}$", message = "Enter the six-digit code")
    private String otp;
}
