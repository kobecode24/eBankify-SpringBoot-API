package com.example.demo.service.impl;


import com.example.demo.entities.User;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import com.example.demo.dto.request.UserRegistrationRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUser(UserRegistrationRequest request) {
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(request.getPassword())
                .role(request.getRole())
                .age(request.getAge())
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = getUserEntity(id);
        return convertToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRegistrationRequest request) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponse> getUsersByRole(Role role) {
        return List.of();
    }

    @Override
    public boolean isEligibleForLoan(Long userId) {
        return false;
    }

    @Override
    public List<UserResponse> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return List.of();
    }

    @Override
    public List<UserResponse> getUsersByIncomeRange(Double minIncome, Double maxIncome) {
        return List.of();
    }

    @Override
    public List<UserResponse> getUsersByMinCreditScore(Integer minCreditScore) {
        return List.of();
    }

    @Override
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    private UserResponse convertToResponse(User user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .age(user.getAge())
                .monthlyIncome(user.getMonthlyIncome())
                .creditScore(user.getCreditScore())
                .build();
    }
}
