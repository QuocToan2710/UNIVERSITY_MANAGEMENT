package com.toan.university_management.configuration;

import com.toan.university_management.entity.Role;
import com.toan.university_management.entity.User;
import com.toan.university_management.repository.PermissionRepository;
import com.toan.university_management.repository.RoleRepository;
import com.toan.university_management.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public void initAdmin(PasswordEncoder passwordEncoder) {
        initDefaultRoles();

        var allPermissions = permissionRepository.findAll();
        
        var adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    var newRole = new Role();
                    newRole.setName("ADMIN");
                    newRole.setDescription("Administrator role");
                    newRole.setPermissions(new HashSet<>(allPermissions));
                    return roleRepository.save(newRole);
                });

        var existingAdmin = userRepository.findByUsername("admin");
        if (existingAdmin.isEmpty()){
            User user = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin"))
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(user);
            log.warn("admin user created with default password: admin");
        } else {
            User adminUser = existingAdmin.get();
            String encodedPassword = adminUser.getPassword();
            if (encodedPassword == null || encodedPassword.isBlank() || !passwordEncoder.matches("admin", encodedPassword)) {
                adminUser.setPassword(passwordEncoder.encode("admin"));
                if (adminUser.getRoles() == null) {
                    adminUser.setRoles(new HashSet<>(Set.of(adminRole)));
                } else if (!adminUser.getRoles().contains(adminRole)) {
                    adminUser.getRoles().add(adminRole);
                }
                userRepository.save(adminUser);
                log.info("admin password auto-synced to BCrypt encoded password: admin");
            }
        }
    }

    private void initDefaultRoles() {
        Map<String, String> defaultRoles = Map.of(
            "USER", "Default user role",
            "TEACHER", "Teacher role",
            "STUDENT", "Student role"
        );

        for (Map.Entry<String, String> entry : defaultRoles.entrySet()) {
            roleRepository.findByName(entry.getKey())
                .orElseGet(() -> {
                    var role = new Role();
                    role.setName(entry.getKey());
                    role.setDescription(entry.getValue());
                    role.setPermissions(new HashSet<>());
                    log.info("Created default role: {}", entry.getKey());
                    return roleRepository.save(role);
                });
        }
    }
}
