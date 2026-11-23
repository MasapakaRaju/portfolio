package com.portfolio.dto.admin;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class ExperienceRequest {
    @NotBlank @Size(max = 180) private String company;
    @NotBlank @Size(max = 180) private String role;
    @NotBlank @Size(max = 180) private String location;
    @NotBlank @Size(max = 100) private String startDate;
    @Size(max = 100) private String endDate;
    @NotNull private Boolean isCurrent;
    @NotBlank @Size(max = 10000) private String description;
    @Size(max = 40) private List<@NotBlank @Size(max = 1000) String> highlights;
    @NotNull @Min(0) @Max(10000) private Integer displayOrder;
}
