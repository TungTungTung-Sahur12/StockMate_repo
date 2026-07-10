package com.stockmate.stockmate_backend.userauthentication.controller;

import com.stockmate.stockmate_backend.userauthentication.dto.*;
import com.stockmate.stockmate_backend.userauthentication.service.UserAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    private final UserAuthService userAuthService;

    public UserAuthController(UserAuthService userAuthService) {
        this.userAuthService = userAuthService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        userAuthService.registerFirstAdmin(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new SimpleMessage("Admin account created successfully. You may now log in."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userAuthService.login(request);
        return ResponseEntity.ok(response);
    }

    record SimpleMessage(String message) {}
}
