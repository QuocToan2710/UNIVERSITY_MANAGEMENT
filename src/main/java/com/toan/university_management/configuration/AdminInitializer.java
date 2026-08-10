package com.toan.university_management.configuration;

import com.toan.university_management.entity.identity.Role;
import com.toan.university_management.entity.identity.User;
import com.toan.university_management.repository.identity.PermissionRepository;
import com.toan.university_management.repository.identity.RoleRepository;
import com.toan.university_management.repository.identity.UserRepository;
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

        var teacherRole = roleRepository.findByName("TEACHER").orElseThrow();
        var studentRole = roleRepository.findByName("STUDENT").orElseThrow();

        // Assign default permissions to roles
        adminRole.setPermissions(new HashSet<>(allPermissions));
        roleRepository.save(adminRole);

        Set<com.toan.university_management.entity.identity.Permission> teacherPerms = allPermissions.stream()
                .filter(p -> "GET".equalsIgnoreCase(p.getMethod()) &&
                        ("STUDENT_MANAGEMENT".equalsIgnoreCase(p.getModule()) ||
                         "COURSE_MANAGEMENT".equalsIgnoreCase(p.getModule()) ||
                         "TEACHER_MANAGEMENT".equalsIgnoreCase(p.getModule())))
                .collect(java.util.stream.Collectors.toSet());
        teacherRole.setPermissions(teacherPerms);
        roleRepository.save(teacherRole);

        Set<com.toan.university_management.entity.identity.Permission> studentPerms = allPermissions.stream()

                .filter(p -> "GET".equalsIgnoreCase(p.getMethod()) &&
                        ("COURSE_MANAGEMENT".equalsIgnoreCase(p.getModule()) ||
                         "TEACHER_MANAGEMENT".equalsIgnoreCase(p.getModule())))
                .collect(java.util.stream.Collectors.toSet());

        studentRole.setPermissions(studentPerms);
        roleRepository.save(studentRole);

        // 1. Admin Account
        initUser("admin", "admin", "admin@university.edu.vn", "Quản Trị Viên Hệ Thống", Set.of(adminRole), passwordEncoder);

        // 2. Teacher Account
        initUser("teacher", "teacher123", "teacher@university.edu.vn", "Giảng Viên Nguyễn Văn B", Set.of(teacherRole), passwordEncoder);

        // 3. Student Account
        initUser("student", "student123", "student@university.edu.vn", "Sinh Viên Trần Thị C", Set.of(studentRole), passwordEncoder);
    }

    private void initUser(String username, String rawPassword, String email, String fullName, Set<Role> roles, PasswordEncoder passwordEncoder) {
        var existingUser = userRepository.findByUsername(username);
        if (existingUser.isEmpty()) {
            User user = User.builder()
                    .username(username)
                    .password(passwordEncoder.encode(rawPassword))
                    .email(email)
                    .fullName(fullName)
                    .roles(roles)
                    .build();
            userRepository.save(user);
            log.info("Initialized default user: {} with role(s): {}", username, roles.stream().map(Role::getName).toList());
        } else {
            User user = existingUser.get();
            if (user.getPassword() == null || user.getPassword().isBlank() || !passwordEncoder.matches(rawPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(rawPassword));
                user.setRoles(new HashSet<>(roles));
                if (user.getFullName() == null || user.getFullName().isBlank()) user.setFullName(fullName);
                if (user.getEmail() == null || user.getEmail().isBlank()) user.setEmail(email);
                userRepository.save(user);
                log.info("Updated default user password/roles for: {}", username);
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

