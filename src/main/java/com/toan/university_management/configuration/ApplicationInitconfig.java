package com.toan.university_management.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitconfig {

    PasswordEncoder passwordEncoder;
    AdminInitializer adminInitializer;

    @Bean
    ApplicationRunner applicationRunner(){
        return args -> adminInitializer.initAdmin(passwordEncoder);
    }
}
