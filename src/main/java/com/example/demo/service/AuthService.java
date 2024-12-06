package com.example.demo.service;


import com.example.demo.dto.request.UserRegistrationRequest;
import com.example.demo.dto.response.AuthenticationResponse;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.dto.request.LoginRequest;


public interface AuthService {
    UserResponse register(UserRegistrationRequest request);
    AuthenticationResponse login(LoginRequest request);
}
