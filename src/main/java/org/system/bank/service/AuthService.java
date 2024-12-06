package org.system.bank.service;

import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.AuthenticationResponse;
import org.system.bank.dto.response.LoginResponse;
import org.system.bank.dto.response.UserResponse;

public interface AuthService {
    UserResponse register(UserRegistrationRequest request);
    AuthenticationResponse login(LoginRequest request);
    AuthenticationResponse refreshToken(String refreshToken);
}
