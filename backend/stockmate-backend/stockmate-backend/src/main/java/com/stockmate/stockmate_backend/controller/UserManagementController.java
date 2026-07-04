package com.stockmate.stockmate_backend.controller;

import com.stockmate.stockmate_backend.dto.*;
import com.stockmate.stockmate_backend.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PostMapping("/staff")
    public ResponseEntity<UserResponse> createStaff(@Valid @RequestBody CreateStaffRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userManagementService.createStaff(request));
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userManagementService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<UserResponse> updateStatus(@PathVariable Long userId,
                                                       @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(userManagementService.updateStatus(userId, request.isActive()));
    }
}