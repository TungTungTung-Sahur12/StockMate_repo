package com.stockmate.stockmate_backend.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class UserResponse {
    private Long userId;
    private String name;
    private String email;
    private String role;

    @JsonProperty("isActive")
    private boolean isActive;

    private LocalDateTime createdAt;
}
