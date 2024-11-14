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
import org.system.bank.exception.DuplicateEmailException;
import org.system.bank.exception.InvalidEmailFormatException;
import org.system.bank.exception.UnderageUserException;
import org.system.bank.exception.WeakPasswordException;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.jpa.UserRepository;
import org.system.bank.service.AuthService;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserResponse register(UserRegistrationRequest request) {
        // Email format validation
        if (!isValidEmail(request.getEmail())) {
            throw new InvalidEmailFormatException("Invalid email format");
        }

        // Check if email exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already registered");
        }

        // Password validation
        if (!isValidPassword(request.getPassword())) {
            throw new WeakPasswordException("Password must contain at least one digit, one lowercase, one uppercase letter, and one special character");
        }

        // Age validation
        if (request.getAge() < 18) {
            throw new UnderageUserException("User must be at least 18 years old");
        }

        User user = userMapper.toEntity(request);
        // Encode the password using jBCrypt before saving
        user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt()));
        user = userRepository.save(user);

        return userMapper.toResponse(user);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return password != null && password.length() >= 6 && pattern.matcher(password).matches();
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
