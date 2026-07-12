package com.stockmate.stockmate_backend.userauthentication.service;

import com.stockmate.stockmate_backend.userauthentication.dto.*;
import com.stockmate.stockmate_backend.userauthentication.repository.AuthUserRepository;
import com.stockmate.stockmate_backend.userauthentication.validator.AuthValidator;
import com.stockmate.stockmate_backend.shared.entity.Role;
import com.stockmate.stockmate_backend.shared.entity.User;
import com.stockmate.stockmate_backend.shared.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserAuthService {

    private final AuthUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthValidator authValidator;

    public UserAuthService(AuthUserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthValidator authValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authValidator = authValidator;
    }

    public void registerFirstAdmin(RegisterRequest request) {
        authValidator.validateRegisterRequest(request);

        if (userRepository.existsByRole(Role.ADMIN)) {
            throw new IllegalStateException("Admin account already exists. Registration is closed.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already in use");
        }

        User admin = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .isActive(true)
                .build();

        userRepository.save(admin);
    }

    public AuthResponse login(LoginRequest request) {
        authValidator.validateLoginRequest(request);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        if (!user.isActive()) {
            throw new IllegalStateException("This account has been deactivated");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getName(), user.getEmail(), user.getRole().name());
    }
}
