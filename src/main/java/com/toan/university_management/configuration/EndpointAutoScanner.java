package com.toan.university_management.configuration;

import com.toan.university_management.annotation.PermissionMeta;
import com.toan.university_management.entity.identity.Permission;
import com.toan.university_management.entity.identity.Role;
import com.toan.university_management.entity.identity.RolePermission;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RolePermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class EndpointAutoScanner implements ApplicationRunner {

    private final RequestMappingHandlerMapping handlerMapping;
    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    @Transactional
    @CacheEvict(value = "publicPermissions", allEntries = true)
    public void run(ApplicationArguments args) {
        log.info("Starting Endpoint Auto-Scan for Dynamic API Permissions...");

        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        List<Permission> newlyCreatedPermissions = new ArrayList<>();

        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
            RequestMappingInfo mappingInfo = entry.getKey();
            HandlerMethod handlerMethod = entry.getValue();

            String controllerClassName = handlerMethod.getBeanType().getName();
            if (controllerClassName.startsWith("org.springframework")) {
                continue;
            }

            Set<RequestMethod> methods = mappingInfo.getMethodsCondition().getMethods();
            Set<String> patterns = getPatterns(mappingInfo);

            if (methods.isEmpty() || patterns.isEmpty()) {
                continue;
            }

            PermissionMeta meta = handlerMethod.getMethodAnnotation(PermissionMeta.class);
            if (meta == null) {
                meta = handlerMethod.getBeanType().getAnnotation(PermissionMeta.class);
            }

            String moduleName = meta != null && !meta.module().isBlank()
                    ? meta.module()
                    : extractModuleName(controllerClassName);

            boolean isPublic = meta != null && meta.isPublic();

            for (RequestMethod method : methods) {
                for (String pattern : patterns) {
                    String permissionCode = "PERM_" + method.name() + "_" + pattern.replace("/", "_").replace("*", "");
                    String permissionName = method.name() + " " + pattern;

                    Optional<Permission> existingOpt = permissionRepository.findByPermissionCode(permissionCode);
                    String description = (meta != null && (!meta.value().isBlank() || !meta.description().isBlank()))
                            ? (!meta.value().isBlank() ? meta.value() : meta.description())
                            : generateDescription(method.name(), pattern, moduleName);

                    if (existingOpt.isEmpty()) {
                        Permission permission = Permission.builder()
                                .permissionCode(permissionCode)
                                .name(permissionName)
                                .method(method.name())
                                .endpoint(pattern)
                                .module(moduleName)
                                .description(description)
                                .isPublic(isPublic)
                                .build();

                        Permission saved = permissionRepository.save(permission);
                        newlyCreatedPermissions.add(saved);
                        log.info("Auto-registered API Permission: [{}] {}", method.name(), pattern);
                    } else {
                        Permission existing = existingOpt.get();
                        boolean updated = false;
                        if (existing.isPublic() != isPublic) {
                            existing.setPublic(isPublic);
                            updated = true;
                        }
                        if (meta != null && !description.equals(existing.getDescription())) {
                            existing.setDescription(description);
                            updated = true;
                        }
                        if (updated) {
                            permissionRepository.save(existing);
                            log.info("Updated metadata for API Permission: [{}] {}", method.name(), pattern);
                        }
                    }
                }
            }
        }

        // Auto-assign non-public permissions to ADMIN role
        if (!newlyCreatedPermissions.isEmpty()) {
            Role adminRole = roleRepository.findByRoleCode("ROLE_ADMIN")
                    .or(() -> roleRepository.findByName("ADMIN"))
                    .orElse(null);

            if (adminRole != null) {
                for (Permission perm : newlyCreatedPermissions) {
                    if (!perm.isPublic()) {
                        rolePermissionRepository.save(RolePermission.builder()
                                .roleId(adminRole.getId())
                                .permissionId(perm.getId())
                                .build());
                    }
                }
                log.info("Auto-synced {} new API permissions to ADMIN role.", newlyCreatedPermissions.size());
            }
        }

        log.info("Endpoint Auto-Scan completed successfully.");
    }

    private Set<String> getPatterns(RequestMappingInfo mappingInfo) {
        if (mappingInfo.getPathPatternsCondition() != null) {
            Set<String> patterns = new HashSet<>();
            mappingInfo.getPathPatternsCondition().getPatterns()
                    .forEach(pattern -> patterns.add(pattern.getPatternString()));
            return patterns;
        }
        if (mappingInfo.getPatternsCondition() != null) {
            return mappingInfo.getPatternsCondition().getPatterns();
        }
        return Collections.emptySet();
    }

    private String extractModuleName(String className) {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        if (simpleName.endsWith("Controller")) {
            simpleName = simpleName.substring(0, simpleName.length() - "Controller".length());
        }
        return simpleName.toUpperCase();
    }

    private String generateDescription(String method, String pattern, String module) {
        String action;
        switch (method) {
            case "GET": action = "Truy xuất danh sách / chi tiết"; break;
            case "POST": action = "Tạo mới dữ liệu"; break;
            case "PUT": action = "Cập nhật dữ liệu"; break;
            case "DELETE": action = "Xóa dữ liệu"; break;
            default: action = "Thao tác dữ liệu"; break;
        }
        return action + " " + module + " (" + pattern + ")";
    }
}
