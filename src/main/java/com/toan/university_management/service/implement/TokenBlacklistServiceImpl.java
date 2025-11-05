package com.toan.university_management.service.implement;


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


    @Override
    public void blacklistToken(String token, long expirationMillis) {
        long expirationSeconds = expirationMillis / 1000;
        redisTemplate.opsForValue().set(token, "BLACKLISTED", expirationSeconds, TimeUnit.SECONDS);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey(token);
    }
}
