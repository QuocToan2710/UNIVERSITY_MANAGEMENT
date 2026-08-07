package com.toan.university_management.service.implement;


import com.toan.university_management.repository.InvalidatedTokenRepository;
import com.toan.university_management.service.TokenBlacklistService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TokenBlacklistServiceImpl implements TokenBlacklistService {
    StringRedisTemplate redisTemplate;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @Override
    public void blacklistToken(String token, long expirationMillis) {
        if (expirationMillis <= 0) return;
        long expirationSeconds = expirationMillis / 1000;
        try {
            redisTemplate.opsForValue().set(token, "BLACKLISTED", Math.max(1, expirationSeconds), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("Could not save blacklisted token to Redis: {}", e.getMessage());
        }
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        try {
            if (Boolean.TRUE.equals(redisTemplate.hasKey(token))) {
                return true;
            }
        } catch (Exception e) {
            log.warn("Redis check failed, falling back to database check: {}", e.getMessage());
        }
        return invalidatedTokenRepository.existsById(token);
    }
}
