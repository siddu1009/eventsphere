package com.eventsphere;

import com.eventsphere.dto.AuthResponse;
import com.eventsphere.dto.LoginRequest;
import com.eventsphere.dto.RegisterRequest;
import com.eventsphere.entity.Role;
import com.eventsphere.entity.UserEntity;
import com.eventsphere.repository.UserRepository;
import com.eventsphere.service.AuthService;
import com.eventsphere.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Test
    void registerAndLoginReturnJwtToken() {
        UserRepository userRepository = mock(UserRepository.class);
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        JwtUtil jwtUtil = new JwtUtil();
        AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil);

        UserEntity savedUser = new UserEntity();
        savedUser.setEmail("demo@example.com");
        savedUser.setPassword(passwordEncoder.encode("secret123"));
        savedUser.setRole(Role.USER);

        when(userRepository.findByEmail("demo@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserEntity.class))).thenReturn(savedUser);

        RegisterRequest request = new RegisterRequest();
        request.setName("Demo User");
        request.setEmail("demo@example.com");
        request.setPassword("secret123");
        request.setPhone("1234567890");

        AuthResponse response = authService.register(request);

        assertNotNull(response.getToken());
        assertEquals("demo@example.com", response.getEmail());
        assertEquals(Role.USER.name(), response.getRole());
        assertTrue(jwtUtil.validateToken(response.getToken()));

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("demo@example.com");
        loginRequest.setPassword("secret123");
        when(userRepository.findByEmail("demo@example.com")).thenReturn(Optional.of(savedUser));

        AuthResponse loginResponse = authService.login(loginRequest);
        assertNotNull(loginResponse.getToken());
        assertTrue(jwtUtil.validateToken(loginResponse.getToken()));
    }
}
