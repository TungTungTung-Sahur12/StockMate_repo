package com.stockmate.stockmate_backend.usermanagement.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CreateStaffRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Temporary password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
