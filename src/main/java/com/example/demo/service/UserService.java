package com.example.demo.service;



import com.example.demo.entities.User;
import com.example.demo.dto.request.UserRegistrationRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.enums.Role;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRegistrationRequest request);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRegistrationRequest request);
    void deleteUser(Long id);
    List<UserResponse> getAllUsers();
    List<UserResponse> getUsersByRole(Role role);
    boolean isEligibleForLoan(Long userId);
    List<UserResponse> getUsersByAgeRange(Integer minAge, Integer maxAge);
    List<UserResponse> getUsersByIncomeRange(Double minIncome, Double maxIncome);
    List<UserResponse> getUsersByMinCreditScore(Integer minCreditScore);
    User getUserEntity(Long userId);
}