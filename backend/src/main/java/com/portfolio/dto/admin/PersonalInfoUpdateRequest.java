package com.portfolio.dto.admin;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PersonalInfoUpdateRequest {
    @NotBlank @Size(max = 120) private String name;
    @NotBlank @Size(max = 160) private String title;
    @NotBlank @Size(max = 500) private String tagline;
    @NotBlank @Size(max = 160) private String location;
    @NotBlank @Email @Size(max = 254) private String email;
    @NotBlank @Pattern(regexp = "^\\+?[0-9 ()-]{7,24}$") private String phone;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String githubUrl;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String linkedinUrl;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String leetcodeUrl;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String resumeUrl;
    @NotBlank @Size(max = 10000) private String about;
}
