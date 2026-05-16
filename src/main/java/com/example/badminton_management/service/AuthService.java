package com.example.badminton_management.service;

import com.example.badminton_management.dto.auth.LoginRequest;
import com.example.badminton_management.dto.auth.LoginResponse;
import com.example.badminton_management.dto.auth.ChangePasswordRequest;
import com.example.badminton_management.dto.auth.RegisterRequest;
import com.example.badminton_management.dto.auth.UserProfileResponse;
import com.example.badminton_management.dto.common.SuccessResponse;
import com.example.badminton_management.exception.BadRequestException;
import com.example.badminton_management.exception.ResourceNotFoundException;
import com.example.badminton_management.model.Role;
import com.example.badminton_management.model.User;
import com.example.badminton_management.repository.RoleRepository;
import com.example.badminton_management.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public SuccessResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) throw new BadRequestException("Username already exists");
        if(userRepository.existsByEmail(request.getEmail()))throw new BadRequestException("Email already exists");
        if(userRepository.existsByPhoneNumber(request.getPhoneNumber()))throw new BadRequestException("Phone number already exists");

        Role role = roleRepository.findByName("USER")
                .orElseThrow(()-> new ResourceNotFoundException("Default role USER not found"));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setGender(request.getGender());

        userRepository.save(user);

        return new SuccessResponse("Register Success: " + request.getFullName());
    }

    public LoginResponse login (LoginRequest request){
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new BadRequestException("Invalid username"));

        boolean isPasswordMatch = passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        );

        if(!isPasswordMatch)throw new BadRequestException("Invalid password");


        String token = jwtService.generateToken(user.getUsername());

        return new LoginResponse("Login success", token);
    }

    public UserProfileResponse getCurrentUserProfile(String username) {
        User user = userRepository.findUsernameWithRole(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return new UserProfileResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getPhoneNumber(),
                user.getGender(),
                user.getStatus().name(),
                user.getRole().getName()
        );
    }

    public SuccessResponse changePassword(String username, ChangePasswordRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BadRequestException("User not found"));

        boolean isCurrentPasswordMatch = passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword()
        );

        if (!isCurrentPasswordMatch) {
            throw new BadRequestException("Current password is incorrect");
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("New password must be different from current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return new SuccessResponse("Password changed successfully");
    }

}
