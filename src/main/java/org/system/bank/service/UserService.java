package org.system.bank.service;

import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;

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
