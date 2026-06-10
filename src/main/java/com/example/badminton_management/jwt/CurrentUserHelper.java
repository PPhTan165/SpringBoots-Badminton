package com.example.badminton_management.jwt;

import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.model.User;
import com.example.badminton_management.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserHelper {
    private final UserRepository userRepository;

    public CurrentUserHelper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Authentication getAuthentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BadRequestException("Unauthenticated");
        }

        return authentication;
    }

    public String getCurrentUsername() {
        String username = getAuthentication().getName();

        if ("anonymousUser".equals(username)) {
            throw new BadRequestException("Unauthenticated");
        }

        return username;
    }

    public User getCurrentUser() {
        return userRepository.findByUsername(getCurrentUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }
}
