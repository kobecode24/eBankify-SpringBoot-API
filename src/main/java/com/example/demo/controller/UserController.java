package com.example.demo.controller;


import com.example.demo.dto.request.UserRegistrationRequest;
import com.example.demo.dto.response.UserResponse;
import com.example.demo.enums.Role;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
@CrossOrigin(origins = {"http://localhost:63342", "http://localhost:8080"})
@PreAuthorize("isAuthenticated()")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Create new user", description = "Creates a new user in the system. Restricted to admin users.")
    @ApiResponse(responseCode = "200", description = "User created successfully")
    @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @Operation(summary = "Get user by ID", description = "Retrieves user details by ID. Access restricted to admin, employee, or the user themselves.")
    @PreAuthorize("@userSecurity.canAccessUserData(#id)")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(summary = "Update user", description = "Updates user details. Access restricted to admin or the user themselves.")
    @PreAuthorize("@userSecurity.canModifyUser(#id)")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "Delete user", description = "Deletes a user from the system. Restricted to admin users.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Get all users", description = "Retrieves all users in the system. Restricted to admin and employee users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @Operation(summary = "Get users by role", description = "Retrieves users filtered by role. Restricted to admin and employee users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @Operation(summary = "Get users by age range", description = "Retrieves users within specified age range. Restricted to admin and employee users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/age-range")
    public ResponseEntity<List<UserResponse>> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        return ResponseEntity.ok(userService.getUsersByAgeRange(minAge, maxAge));
    }

    @Operation(summary = "Get users by income range", description = "Retrieves users within specified income range. Restricted to admin and employee users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/income-range")
    public ResponseEntity<List<UserResponse>> getUsersByIncomeRange(
            @RequestParam Double minIncome,
            @RequestParam Double maxIncome) {
        return ResponseEntity.ok(userService.getUsersByIncomeRange(minIncome, maxIncome));
    }

    @Operation(summary = "Get users by minimum credit score", description = "Retrieves users with credit score above specified minimum. Restricted to admin and employee users.")
    @PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
    @GetMapping("/credit-score")
    public ResponseEntity<List<UserResponse>> getUsersByMinCreditScore(
            @RequestParam Integer minCreditScore) {
        return ResponseEntity.ok(userService.getUsersByMinCreditScore(minCreditScore));
    }

    @Operation(summary = "Check loan eligibility", description = "Checks if a user is eligible for a loan. Access restricted to admin, employee, or the user themselves.")
    @PreAuthorize("@userSecurity.canAccessUserData(#id)")
    @GetMapping("/{id}/loan-eligibility")
    public ResponseEntity<Boolean> checkLoanEligibility(@PathVariable Long id) {
        return ResponseEntity.ok(userService.isEligibleForLoan(id));
    }
}
