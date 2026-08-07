package com.toan.university_management.configuration;

import com.nimbusds.jwt.SignedJWT;
import com.toan.university_management.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenBlacklistFilter extends OncePerRequestFilter {
    private final TokenBlacklistService tokenBlacklistService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                SignedJWT signedJWT = SignedJWT.parse(token);
                String jti = signedJWT.getJWTClaimsSet().getJWTID();
                if (jti != null && tokenBlacklistService.isTokenBlacklisted(jti)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"code\": 1006, \"message\": \"Token is blacklisted\"}");
                    return;
                }
            } catch (Exception e) {
                log.debug("Token parsing failed in filter: {}", e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
