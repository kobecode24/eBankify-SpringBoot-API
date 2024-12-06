package com.example.demo.security.expression;


import com.example.demo.entities.User;
import com.example.demo.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurityExpression extends SecurityExpressionRoot {

    public boolean canAccessUserData(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.ADMIN ||
                currentUser.getRole() == Role.EMPLOYEE ||
                currentUser.getUserId().equals(userId);
    }

    public boolean canModifyUser(Long userId) {
        User currentUser = getCurrentUser();
        return currentUser.getRole() == Role.ADMIN ||
                currentUser.getUserId().equals(userId);
    }
}
