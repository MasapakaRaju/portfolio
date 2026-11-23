package com.portfolio.dto.admin;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactMessageReadRequest {
    @NotNull
    private Boolean isRead;
}
