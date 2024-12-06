package com.example.demo.service.impl;


import com.example.demo.config.JwtService;
import com.example.demo.dto.request.LoginRequest;
import com.example.demo.entities.User;
import com.example.demo.exception.AuthenticationException;
import com.example.demo.repository.UserRepository;
import com.example.demo.dto.request.UserRegistrationRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .age(request.getAge())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Override
    public AuthenticationResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return AuthenticationResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .name(user.getName())
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .age(user.getAge())
                .monthlyIncome(user.getMonthlyIncome())
                .creditScore(user.getCreditScore())
                .build();
    }
}
