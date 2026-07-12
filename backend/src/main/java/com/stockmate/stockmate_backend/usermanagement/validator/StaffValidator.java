package com.stockmate.stockmate_backend.usermanagement.validator;

import com.stockmate.stockmate_backend.usermanagement.dto.CreateStaffRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StaffValidator {

    public void validateCreateStaffRequest(CreateStaffRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Staff request is required");
        }

        if (!StringUtils.hasText(request.getName())) {
            throw new IllegalArgumentException("Name is required");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email format is invalid");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException("Temporary password is required");
        }

        if (request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }
    }
}
