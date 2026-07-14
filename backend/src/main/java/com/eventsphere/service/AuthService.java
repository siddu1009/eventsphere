package com.eventsphere.service;

import com.eventsphere.dto.AuthResponse;
import com.eventsphere.dto.LoginRequest;
import com.eventsphere.dto.RegisterRequest;
import com.eventsphere.entity.Role;
import com.eventsphere.entity.UserEntity;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Locale;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse register(RegisterRequest request) {
        if (request.getEmail() == null || request.getEmail().isBlank() || request.getPassword() == null || request.getPassword().length() < 8) {
            throw new IllegalArgumentException("Email and a password of at least 8 characters are required");
        }
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }
        UserEntity user = new UserEntity();
        user.setName(request.getName().trim());
        user.setEmail(email);
        user.setPhone(request.getPhone() == null ? null : request.getPhone().trim());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getName());
    }

    public AuthResponse login(LoginRequest request) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        if (userOpt.isEmpty() || !passwordEncoder.matches(request.getPassword(), userOpt.get().getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        UserEntity user = userOpt.get();
        if (!user.isEnabled()) {
            throw new IllegalArgumentException("This account is disabled");
        }
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), user.getName());
    }
}
