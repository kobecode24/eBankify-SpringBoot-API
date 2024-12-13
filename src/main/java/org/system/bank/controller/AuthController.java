package org.system.bank.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.AuthenticationResponse;
import org.system.bank.dto.response.KeycloakTokenResponse;
import org.system.bank.keylock.KeycloakService;
import org.system.bank.otp.OtpService;

import jakarta.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
//@CrossOrigin(origins = "*")
public class AuthController {

    private final KeycloakService keycloakService;
    private final OtpService otpService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @Valid @RequestBody UserRegistrationRequest request) {
        try {
            KeycloakTokenResponse tokenResponse = keycloakService.registerUser(request);

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .build());
        } catch (Exception e) {
            log.error("Registration failed", e);
            String errorMessage = e.getMessage();
            if (errorMessage.contains("already registered")) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, errorMessage);
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Registration failed: " + errorMessage);
        }
    }

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.debug("Login attempt for user: {}", request.getEmail());
            KeycloakTokenResponse tokenResponse = keycloakService.getToken(request);
            log.debug("Login successful for user: {}", request.getEmail());

            return ResponseEntity.ok(AuthenticationResponse.builder()
                    .token(tokenResponse.getAccessToken())
                    .refreshToken(tokenResponse.getRefreshToken())
                    .email(request.getEmail())
                    .build());
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getEmail(), e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }
    }}