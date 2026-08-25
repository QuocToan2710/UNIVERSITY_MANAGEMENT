package com.toan.university_management.common.util;

import com.toan.university_management.constant.RoleConstants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public final class SecurityUtils {

    private SecurityUtils() {
        // Prevent instantiation
    }

    public static Optional<String> getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equalsIgnoreCase(auth.getName())) {
            return Optional.empty();
        }
        return Optional.ofNullable(auth.getName());
    }

    public static Set<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return Collections.emptySet();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
    }

    public static boolean hasRole(String role) {
        Set<String> roles = getCurrentUserRoles();
        String targetRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return roles.contains(targetRole) || roles.contains(role);
    }

    public static boolean isAdmin() {
        return hasRole(RoleConstants.ROLE_ADMIN);
    }

    public static boolean isTeacher() {
        return hasRole(RoleConstants.ROLE_TEACHER);
    }

    public static boolean isStudent() {
        return hasRole(RoleConstants.ROLE_STUDENT);
    }
}
