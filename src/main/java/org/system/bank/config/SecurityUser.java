package org.system.bank.config;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.system.bank.entity.User;

import java.util.*;


public record SecurityUser(User user) implements OAuth2User, UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        // Add the user's primary role
        authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
        // Add any additional Keycloak roles if present
        if (user.getKeycloakRoles() != null) {
            user.getKeycloakRoles().forEach(role ->
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role))
            );
        }
        return authorities;
    }

    @Override
    public Map<String, Object> getAttributes() {
        // Return OAuth2 attributes from Keycloak
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", user.getUserId().toString());
        attributes.put("email", user.getEmail());
        attributes.put("name", user.getName());
        return attributes;
    }

    @Override
    public String getName() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}
