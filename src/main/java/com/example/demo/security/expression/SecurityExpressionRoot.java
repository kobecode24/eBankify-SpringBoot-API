package com.example.demo.security.expression;


import com.example.demo.config.SecurityUser;
import com.example.demo.entities.User;
import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityExpressionRoot {

    protected User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ((SecurityUser) authentication.getPrincipal()).user();
    }

    protected boolean isAdmin() {
        return getCurrentUser().getRole() == Role.ADMIN;
    }

    protected boolean isEmployee() {
        return getCurrentUser().getRole() == Role.EMPLOYEE;
    }
}
