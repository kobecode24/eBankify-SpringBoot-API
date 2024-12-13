package org.system.bank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.system.bank.dto.response.KeycloakTokenResponse;
import org.system.bank.entity.User;
import org.system.bank.enums.Role;
import org.system.bank.exception.AuthenticationException;
import org.system.bank.keylock.KeycloakService;
import org.system.bank.repository.jpa.UserRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final KeycloakService keycloakService;
    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository
                .findByEmail(email)
                .map(user -> {
                    try {
                        KeycloakTokenResponse tokenResponse = keycloakService.verifyUser(email);
                        updateUserAuthorities(user, tokenResponse);
                        return new SecurityUser(user);
                    } catch (Exception e) {
                        throw new org.springframework.security.core.AuthenticationException(
                                "Failed to verify user with Keycloak: " + e.getMessage()) {};
                    }
                })
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    private void updateUserAuthorities(User user, KeycloakTokenResponse tokenResponse) {
        Set<String> keycloakRoles = extractRolesFromToken(tokenResponse.getAccessToken());
        user.setKeycloakRoles(keycloakRoles);
        user.updateRole(mapHighestRole(keycloakRoles));
        userRepository.save(user);
    }

    private Set<String> extractRolesFromToken(String accessToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtService.getSignInKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) claims.get("roles", List.class);
        return new HashSet<>(roles != null ? roles : Collections.emptyList());
    }

    private Role mapHighestRole(Set<String> keycloakRoles) {
        if (keycloakRoles.contains("ADMIN")) return Role.ADMIN;
        if (keycloakRoles.contains("EMPLOYEE")) return Role.EMPLOYEE;
        return Role.USER;
    }
}
