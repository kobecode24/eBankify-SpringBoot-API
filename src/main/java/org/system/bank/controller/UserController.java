package org.system.bank.controller;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.tags.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.UserResponse;
import org.system.bank.enums.Role;
import org.system.bank.service.UserService;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "APIs for managing users")
@CrossOrigin(origins = {"http://localhost:63342", "http://localhost:8080"})
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRegistrationRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users in the system"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users found successfully",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserResponse>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    @GetMapping("/age-range")
    public ResponseEntity<List<UserResponse>> getUsersByAgeRange(
            @RequestParam Integer minAge,
            @RequestParam Integer maxAge) {
        return ResponseEntity.ok(userService.getUsersByAgeRange(minAge, maxAge));
    }

    @GetMapping("/income-range")
    public ResponseEntity<List<UserResponse>> getUsersByIncomeRange(
            @RequestParam Double minIncome,
            @RequestParam Double maxIncome) {
        return ResponseEntity.ok(userService.getUsersByIncomeRange(minIncome, maxIncome));
    }

    @GetMapping("/credit-score")
    public ResponseEntity<List<UserResponse>> getUsersByMinCreditScore(
            @RequestParam Integer minCreditScore) {
        return ResponseEntity.ok(userService.getUsersByMinCreditScore(minCreditScore));
    }

    @GetMapping("/{id}/loan-eligibility")
    public ResponseEntity<Boolean> checkLoanEligibility(@PathVariable Long id) {
        return ResponseEntity.ok(userService.isEligibleForLoan(id));
    }
}
