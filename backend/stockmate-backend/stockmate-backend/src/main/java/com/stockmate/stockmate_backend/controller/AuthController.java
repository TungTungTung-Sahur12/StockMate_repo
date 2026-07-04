package com.stockmate.stockmate_backend.controller;

import com.stockmate.stockmate_backend.dto.*;
import com.stockmate.stockmate_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        authService.registerFirstAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleMessage("Admin account created successfully. You may now log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    record SimpleMessage(String message) {}
}