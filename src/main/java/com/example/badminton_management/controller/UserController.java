package com.example.badminton_management.controller;

import com.example.badminton_management.dto.auth.ChangePasswordRequest;
import com.example.badminton_management.dto.auth.UserProfileResponse;
import com.example.badminton_management.dto.common.SuccessResponse;
import com.example.badminton_management.service.AuthService;
import com.example.badminton_management.jwt.CurrentUserHelper;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;
    private final CurrentUserHelper currentUserHelper;

    public UserController(AuthService authService, CurrentUserHelper currentUserHelper) {
        this.authService = authService;
        this.currentUserHelper = currentUserHelper;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public UserProfileResponse currentUser(){
        return authService.getCurrentUserProfile(currentUserHelper.getCurrentUsername());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public UserProfileResponse currentAdmin(){
        return authService.getCurrentUserProfile(currentUserHelper.getCurrentUsername());
    }

    @PatchMapping("/password")
    public SuccessResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        return authService.changePassword(currentUserHelper.getCurrentUsername(), request);
    }
}
