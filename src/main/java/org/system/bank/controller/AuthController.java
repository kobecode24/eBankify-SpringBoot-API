package org.system.bank.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.system.bank.config.JwtService;
import org.system.bank.config.SecurityUser;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.AuthenticationResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.exception.AuthenticationException;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.jpa.UserRepository;
import org.system.bank.service.AuthService;
import java.util.Map;

import jakarta.validation.Valid;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/verify")
    @Transactional(readOnly = true)
    public ResponseEntity<?> verifyToken(HttpServletRequest request) {
        try {
            log.debug("Token verification request received");
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("No Bearer token found in Authorization header");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("No valid authorization token found"));
            }

            String token = authHeader.substring(7);
            log.debug("Extracted token: {}", token.substring(0, Math.min(token.length(), 10)) + "...");

            String userEmail = jwtService.extractUsername(token);
            if (userEmail == null) {
                log.warn("Could not extract username from token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token format"));
            }
            log.debug("Token belongs to user: {}", userEmail);

            // Find user and validate token
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            SecurityUser securityUser = new SecurityUser(user);
            if (!jwtService.isTokenValid(token, securityUser)) {
                log.warn("Token validation failed for user: {}", userEmail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ErrorResponse("Invalid token"));
            }

            // Set authentication in security context
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            securityUser,
                            null,
                            securityUser.getAuthorities()
                    );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            log.debug("Successfully verified token for user ID: {}", user.getUserId());
            return ResponseEntity.ok(userMapper.toResponse(user));

        } catch (Exception e) {
            log.error("Token verification failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Token verification failed: " + e.getMessage()));
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthenticationResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshToken == null) {
            return ResponseEntity.badRequest()
                    .body(AuthenticationResponse.builder()
                            .build());
        }

        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }

    @Data
    @AllArgsConstructor
    private static class ErrorResponse {
        private String message;
    }
}