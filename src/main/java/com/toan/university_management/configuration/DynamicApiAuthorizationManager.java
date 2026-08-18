package com.toan.university_management.configuration;

import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.repository.identity.PermissionRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicApiAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final PermissionRepository permissionRepository;
    private final com.toan.university_management.repository.identity.UserRepository userRepository;
    private final com.toan.university_management.repository.identity.UserRoleRepository userRoleRepository;
    private final com.toan.university_management.repository.identity.RolePermissionRepository rolePermissionRepository;

    private static final Set<String> PUBLIC_POST_ENDPOINTS = Set.of(
            "/auth/token", "/auth/introspect", "/auth/logout", "/auth/refresh", "/users"
    );

    @Override
    public AuthorizationDecision check(Supplier<Authentication> authenticationSupplier, RequestAuthorizationContext context) {
        HttpServletRequest request = context.getRequest();
        String method = request.getMethod();
        String path = request.getRequestURI();

        // 1. Allow OPTIONS pre-flight CORS requests
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return new AuthorizationDecision(true);
        }

        // 1.5. Allow Swagger UI & OpenAPI Spec Endpoints
        if (antPathMatcher.match("/v3/api-docs/**", path) ||
            antPathMatcher.match("/swagger-ui/**", path) ||
            antPathMatcher.match("/swagger-ui.html", path)) {
            return new AuthorizationDecision(true);
        }

        // 2. Allow Public POST endpoints & DB marked isPublic permissions
        if ("POST".equalsIgnoreCase(method) && PUBLIC_POST_ENDPOINTS.contains(path)) {
            return new AuthorizationDecision(true);
        }

        // Check if endpoint matches any permission marked isPublic=true in DB
        try {
            var publicPerms = permissionRepository.findByIsPublicTrue();
            for (var perm : publicPerms) {
                if (perm.getMethod() != null && perm.getMethod().equalsIgnoreCase(method)
                        && perm.getEndpoint() != null && antPathMatcher.match(perm.getEndpoint(), path)) {
                    return new AuthorizationDecision(true);
                }
            }
        } catch (Exception e) {
            log.debug("Error checking public permissions: {}", e.getMessage());
        }

        Authentication authentication = authenticationSupplier.get();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return new AuthorizationDecision(false);
        }

        // Allow any authenticated user to perform GET or read-only search/combo/export/all requests
        if ("GET".equalsIgnoreCase(method) || path.endsWith("/search") || path.endsWith("/combo") || path.endsWith("/export") || path.endsWith("/all") || path.contains("/myInfo")) {
            return new AuthorizationDecision(true);
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 3. Admin wildcard override
        for (GrantedAuthority authority : authorities) {
            String authStr = authority.getAuthority();
            if (authStr != null && ("ROLE_ADMIN".equalsIgnoreCase(authStr) || "ADMIN".equalsIgnoreCase(authStr))) {
                return new AuthorizationDecision(true);
            }
        }

        // 4. Dynamic API permission matching via User -> UserRole (roleId) -> RolePermission (permissionId)
        try {
            String username = authentication.getName();
            var userOpt = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByUsernameIgnoreCase(username));

            if (userOpt.isPresent()) {
                String userId = userOpt.get().getId();
                List<com.toan.university_management.entity.identity.UserRole> userRoles = userRoleRepository.findByUserId(userId);
                Set<Long> roleIds = userRoles.stream()
                        .map(com.toan.university_management.entity.identity.UserRole::getRoleId)
                        .collect(Collectors.toSet());

                if (!roleIds.isEmpty()) {
                    List<com.toan.university_management.entity.identity.RolePermission> rolePerms = rolePermissionRepository.findByRoleIdIn(roleIds);
                    Set<Long> permIds = rolePerms.stream()
                            .map(com.toan.university_management.entity.identity.RolePermission::getPermissionId)
                            .collect(Collectors.toSet());

                    if (!permIds.isEmpty()) {
                        List<Permission> userPermissions = permissionRepository.findAllByIdIn(permIds);
                        for (Permission perm : userPermissions) {
                            if (perm.getMethod() != null && perm.getMethod().equalsIgnoreCase(method)
                                    && perm.getEndpoint() != null && antPathMatcher.match(perm.getEndpoint(), path)) {
                                return new AuthorizationDecision(true);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.debug("Error checking user dynamic permissions: {}", e.getMessage());
        }

        log.warn("Access Denied for User [{}] - Endpoint: [{}] {}", authentication.getName(), method, path);
        return new AuthorizationDecision(false);
    }
}
