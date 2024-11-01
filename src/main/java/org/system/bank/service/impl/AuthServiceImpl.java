package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.LoginResponse;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.exception.AuthenticationException;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.UserRepository;
import org.system.bank.service.AuthService;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered");
        }

        User user = userMapper.toEntity(request);
        // Encode the password using jBCrypt before saving
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        // Verify password using jBCrypt
        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Invalid credentials");
        }

        return new LoginResponse(
                user.getUserId(),
                user.getName(),
                user.getEmail(),
                user.getRole()
        );
    }
}
