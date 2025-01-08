package org.system.bank.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.system.bank.mapper.UserMapper;
import org.system.bank.repository.jpa.UserRepository;
import org.system.bank.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final LoanEligibilityService loanEligibilityService;
    private final PasswordEncoder passwordEncoder; // Use Spring's PasswordEncoder instead of BCrypt directly

    @Override
    public UserResponse createUser(UserRegistrationRequest request) {
        User user = userMapper.toEntity(request);
        User savedUser = userRepository.save(user);
        savedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = getUserEntity(id);
        return userMapper.toResponse(user);
    }

    @Override
    public User getUserEntity(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + id));
    }

    @Override
    public UserResponse updateUser(Long id, UserRegistrationRequest request) {
        getUserEntity(id);

        User userToUpdate = userMapper.toEntity(request);
        userToUpdate.setUserId(id);

        User updatedUser = userRepository.save(userToUpdate);
        updatedUser.setPassword(passwordEncoder.encode(request.getPassword()));
        return userMapper.toResponse(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userMapper.toResponseList(userRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(Role role) {
        return userMapper.toResponseList(userRepository.findByRole(role));
    }

    @Override
    public boolean isEligibleForLoan(Long userId) {
        User user = getUserEntity(userId);
        if (!loanEligibilityService.isEligibleForBasicCriteria(user)) {
            return false;
        }
        return !loanEligibilityService.hasActiveLoan(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByAgeRange(Integer minAge, Integer maxAge) {
        return userMapper.toResponseList(userRepository.findByAgeBetween(minAge, maxAge));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByIncomeRange(Double minIncome, Double maxIncome) {
        return userMapper.toResponseList(userRepository.findByMonthlyIncomeBetween(minIncome, maxIncome));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByMinCreditScore(Integer minCreditScore) {
        return userMapper.toResponseList(userRepository.findByCreditScoreGreaterThanEqual(minCreditScore));
    }
}
