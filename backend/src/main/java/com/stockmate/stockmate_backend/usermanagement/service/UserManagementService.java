package com.stockmate.stockmate_backend.usermanagement.service;

import com.stockmate.stockmate_backend.usermanagement.dto.*;
import com.stockmate.stockmate_backend.usermanagement.repository.StaffRepository;
import com.stockmate.stockmate_backend.usermanagement.validator.StaffValidator;
import com.stockmate.stockmate_backend.shared.entity.Role;
import com.stockmate.stockmate_backend.shared.entity.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserManagementService {

    private final StaffRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final StaffValidator staffValidator;

    public UserManagementService(StaffRepository userRepository, PasswordEncoder passwordEncoder, StaffValidator staffValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.staffValidator = staffValidator;
    }

    public UserResponse createStaff(CreateStaffRequest request) {
        staffValidator.validateCreateStaffRequest(request);
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User staff = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.STAFF)
                .isActive(true)
                .build();

        User saved = userRepository.save(staff);
        return toResponse(saved);
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse updateUser(Long userId, UpdateStaffRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("Cannot edit an Admin account");
        }

        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    public UserResponse updateStatus(Long userId, boolean isActive) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() == Role.ADMIN) {
            throw new IllegalStateException("Cannot deactivate an Admin account");
        }

        user.setActive(isActive);
        User saved = userRepository.save(user);
        return toResponse(saved);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
