package com.toan.university_management.configuration;

import com.toan.university_management.entity.User;
import com.toan.university_management.entity.Role;
import com.toan.university_management.repository.RoleRepository;
import com.toan.university_management.repository.UserRepository;
import com.toan.university_management.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitconfig {

    PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(prefix = "spring",
            value = "datasource.driver-class-name",
            havingValue = "com.mysql.cj.jdbc.Driver"
    )
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository, PermissionRepository permissionRepository){
        return args ->{
            if ( userRepository.findByUsername("admin").isEmpty()){
                // Get all permissions or create default ones
                var allPermissions = permissionRepository.findAll();
                
                var adminRole = roleRepository.findByName("ADMIN")
                        .orElseGet(() -> {
                            var newRole = new Role();
                            newRole.setName("ADMIN");
                            newRole.setDescription("Administrator role");
                            newRole.setPermissions(Set.copyOf(allPermissions));
                            return roleRepository.save(newRole);
                        });
                
                // If role already exists, add all permissions to it
                if (!allPermissions.isEmpty() && (adminRole.getPermissions() == null || adminRole.getPermissions().isEmpty())) {
                    adminRole.setPermissions(Set.copyOf(allPermissions));
                    roleRepository.save(adminRole);
                }

                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .roles(Set.of(adminRole))
                        .build();

                userRepository.save(user);
                log.warn("admin user has been create with default password: admin, please change it");
            }
        };
    }

}
