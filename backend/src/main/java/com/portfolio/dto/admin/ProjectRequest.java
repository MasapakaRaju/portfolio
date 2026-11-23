package com.portfolio.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProjectRequest {
    @NotBlank @Size(max = 180) private String name;
    @NotBlank @Size(max = 10000) private String description;
    @NotBlank @Size(max = 1000) private String techStack;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String githubUrl;
    @Size(max = 500) @Pattern(regexp = "^(https?://\\S*)?$", message = "Use a valid http or https URL") private String demoUrl;
    @NotNull private Boolean isFeatured;
    @NotNull @Min(0) @Max(10000) private Integer displayOrder;
}
