package com.example.invoiceserver.service;

import com.example.invoiceserver.dto.request.SignupRequest;
import com.example.invoiceserver.entity.User;
import com.example.invoiceserver.entity.UserRole;
import com.example.invoiceserver.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(SignupRequest signupRequest) {
        // Check if user already exists
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        // Create new user
        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setFullName(signupRequest.getFullName());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        
        // Set default role to USER
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);
        
        // If explicitly registering an admin
        if (signupRequest.getIsAdmin() != null && signupRequest.getIsAdmin()) {
            roles.add(UserRole.ROLE_ADMIN);
        }
        
        user.setRoles(roles);
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
} 