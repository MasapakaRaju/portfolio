package com.portfolio.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SkillRequest {
    @NotBlank @Size(max = 100) private String name;
    @NotBlank @Size(max = 100) private String category;
    @Min(0) @Max(100) private Integer proficiency;
}
