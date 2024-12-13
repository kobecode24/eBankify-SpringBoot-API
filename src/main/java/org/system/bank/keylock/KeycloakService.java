package org.system.bank.keylock;

import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.system.bank.config.KeycloakProperties;
import org.system.bank.dto.request.LoginRequest;
import org.system.bank.dto.request.UserRegistrationRequest;
import org.system.bank.dto.response.KeycloakTokenResponse;
import org.system.bank.enums.Role;
import org.system.bank.exception.AuthenticationException;
import org.system.bank.exception.KeycloakRegistrationException;
import org.system.bank.exception.UserAlreadyExistsException;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KeycloakService {
    private final Keycloak adminClient;
    private final WebClient webClient;
    private final KeycloakProperties properties;
    private final WebClient keycloakWebClient;
    private final Keycloak adminKeycloak;


    public KeycloakTokenResponse registerUser(UserRegistrationRequest request) {
        try {
            if (userExists(request.getEmail())) {
                throw new UserAlreadyExistsException("Email already registered");
            }

            // Create user representation with required attributes
            UserRepresentation user = new UserRepresentation();
            user.setEnabled(true);
            user.setUsername(request.getEmail());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getName());
            user.setEmailVerified(true);

            // Add attributes
            Map<String, List<String>> attributes = new HashMap<>();
            attributes.put("age", Collections.singletonList(request.getAge().toString()));
            attributes.put("monthlyIncome", Collections.singletonList(request.getMonthlyIncome().toString()));
            attributes.put("creditScore", Collections.singletonList(request.getCreditScore().toString()));
            user.setAttributes(attributes);

            // Set credentials with required actions
            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(request.getPassword());
            credential.setTemporary(false);
            user.setCredentials(Collections.singletonList(credential));

            // Clear any required actions
            user.setRequiredActions(new ArrayList<>());

            // Create user
            Response response = adminKeycloak.realm(properties.getRealm())
                    .users().create(user);

            if (response.getStatus() != 201) {
                throw new KeycloakRegistrationException("Failed to create user");
            }

            String userId = CreatedResponseUtil.getCreatedId(response);

            // Get the user management interface
            UserResource userResource = adminKeycloak.realm(properties.getRealm())
                    .users().get(userId);

            // Assign role
            assignRole(userId, request.getRole());

            // Reset password to ensure it's properly set
            resetPassword(userResource, request.getPassword());

            // Get tokens for the new user
            return authenticateUser(request.getEmail(), request.getPassword());

        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            throw new KeycloakRegistrationException("Registration failed: " + e.getMessage());
        }
    }

    private void resetPassword(UserResource userResource, String password) {
        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        userResource.resetPassword(credential);

        // Remove any required actions
        userResource.update(userResource.toRepresentation());
    }

    public KeycloakTokenResponse authenticateUser(String username, String password) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "password");
        formData.add("client_id", properties.getResource());
        formData.add("client_secret", properties.getCredentials().getSecret());
        formData.add("username", username);
        formData.add("password", password);

        return keycloakWebClient
                .post()
                .uri("/realms/" + properties.getRealm() + "/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue(formData)
                .retrieve()
                .bodyToMono(KeycloakTokenResponse.class)
                .doOnError(error -> log.error("Authentication failed: {}", error.getMessage()))
                .block();
    }

    private void assignRole(String userId, Role role) {
        try {
            RealmResource realmResource = adminKeycloak.realm(properties.getRealm());
            RoleRepresentation roleRep = realmResource.roles()
                    .get(role.name())
                    .toRepresentation();

            realmResource.users().get(userId)
                    .roles()
                    .realmLevel()
                    .add(Collections.singletonList(roleRep));

        } catch (Exception e) {
            log.error("Failed to assign role: {}", e.getMessage());
            throw new KeycloakRegistrationException("Failed to assign role");
        }
    }

    private UserRepresentation createUserRepresentation(UserRegistrationRequest request) {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(request.getEmail());
        user.setEmail(request.getEmail());
        user.setFirstName(request.getName());
        user.setEmailVerified(true);

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(request.getPassword());
        credential.setTemporary(false);
        user.setCredentials(Collections.singletonList(credential));

        return user;
    }

    public boolean userExists(String email) {
        try {
            List<UserRepresentation> users = adminClient.realm(properties.getRealm())
                    .users()
                    .search(email);
            return users != null && !users.isEmpty() &&
                    users.stream().anyMatch(user -> email.equals(user.getEmail()));
        } catch (Exception e) {
            log.error("Error checking user existence in Keycloak for email {}: {}", email, e.getMessage());
            throw new KeycloakRegistrationException("Failed to check user existence: " + e.getMessage());
        }
    }

    public KeycloakTokenResponse verifyUser(String email) {
        try {
            if (!userExists(email)) {
                throw new AuthenticationException("User not found in Keycloak");
            }

            UserRepresentation user = adminClient.realm(properties.getRealm())
                    .users()
                    .search(email)
                    .stream()
                    .filter(u -> email.equals(u.getEmail()))
                    .findFirst()
                    .orElseThrow(() -> new AuthenticationException("User not found"));

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", properties.getResource());
            formData.add("client_secret", properties.getCredentials().getSecret());
            formData.add("username", email);

            return keycloakWebClient
                    .post()
                    .uri("/realms/" + properties.getRealm() + "/protocol/openid-connect/token")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(KeycloakTokenResponse.class)
                    .doOnError(error -> {
                        log.error("Failed to verify user {}: {}", email, error.getMessage());
                        throw new AuthenticationException("Failed to verify user: " + error.getMessage());
                    })
                    .block();
        } catch (Exception e) {
            log.error("Error verifying user in Keycloak: {}", e.getMessage());
            throw new AuthenticationException("Failed to verify user: " + e.getMessage());
        }
    }

    public KeycloakTokenResponse getToken(LoginRequest request) {
        try {
            log.debug("Attempting to authenticate user: {}", request.getEmail());

            // Build form data exactly like the working CURL request
            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("grant_type", "password");
            formData.add("client_id", properties.getResource());
            formData.add("client_secret", properties.getCredentials().getSecret());
            formData.add("username", request.getEmail());
            formData.add("password", request.getPassword());
            // Add these additional scopes that were in the successful CURL request
            formData.add("scope", "email profile");

            String tokenEndpoint = String.format("%s/realms/%s/protocol/openid-connect/token",
                    properties.getAuthServerUrl(), properties.getRealm());

            log.debug("Token request to: {}", tokenEndpoint);
            log.debug("Form data: {}", formData);

            return keycloakWebClient
                    .post()
                    .uri(tokenEndpoint)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .bodyValue(formData)
                    .retrieve()
                    .bodyToMono(KeycloakTokenResponse.class)
                    .doOnError(error -> {
                        if (error instanceof WebClientResponseException wcError) {
                            log.error("Authentication error. Status: {}, Body: {}",
                                    wcError.getStatusCode(),
                                    wcError.getResponseBodyAsString());
                        }
                    })
                    .block();

        } catch (Exception e) {
            log.error("Authentication error: {}", e.getMessage());
            throw new AuthenticationException("Authentication failed: " + e.getMessage());
        }
    }
}
