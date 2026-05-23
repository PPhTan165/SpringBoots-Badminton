package com.example.badminton_management.controller;

import com.example.badminton_management.dto.auth.ChangePasswordRequest;
import com.example.badminton_management.dto.auth.UserProfileResponse;
import com.example.badminton_management.dto.common.SuccessResponse;
import com.example.badminton_management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/me")
    public UserProfileResponse currentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authService.getCurrentUserProfile(authentication.getName());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public UserProfileResponse currentAdmin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authService.getCurrentUserProfile(authentication.getName());
    }

    @PatchMapping("/password")
    public SuccessResponse changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return authService.changePassword(authentication.getName(), request);
    }
}
