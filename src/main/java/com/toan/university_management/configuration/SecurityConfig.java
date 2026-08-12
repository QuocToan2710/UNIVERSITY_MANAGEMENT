package com.toan.university_management.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.toan.university_management.dto.response.ApiResponse;
import com.toan.university_management.exception.ErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableCaching
public class SecurityConfig {
    private final CustomJwtDecoder customJwtDecoder;
    private final DynamicApiAuthorizationManager dynamicApiAuthorizationManager;

    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:5173,http://localhost:4173}")
    private List<String> allowedOrigins;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder, DynamicApiAuthorizationManager dynamicApiAuthorizationManager) {
        this.customJwtDecoder = customJwtDecoder;
        this.dynamicApiAuthorizationManager = dynamicApiAuthorizationManager;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, TokenBlacklistFilter tokenBlacklistFilter) throws Exception {

        httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()));
        httpSecurity.csrf(AbstractHttpConfigurer::disable);

        httpSecurity.authorizeHttpRequests(request ->
                request.anyRequest().access(dynamicApiAuthorizationManager));

        httpSecurity.addFilterBefore(tokenBlacklistFilter,
                org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        httpSecurity.oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwtConfigurer ->
                                jwtConfigurer.decoder(customJwtDecoder)
                                        .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
        );

        httpSecurity.exceptionHandling(exception -> exception
                .accessDeniedHandler((request, response, ex) ->
                        writeErrorResponse(response, HttpServletResponse.SC_FORBIDDEN, ErrorCode.UNAUTHORIZED))
                .authenticationEntryPoint((request, response, ex) ->
                        writeErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorCode.UNAUTHENTICATED))
        );

        return httpSecurity.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    int status,
                                    ErrorCode errorCode) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
        new ObjectMapper().writeValue(response.getOutputStream(), apiResponse);
    }
}
