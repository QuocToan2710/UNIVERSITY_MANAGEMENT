package com.toan.university_management.service.otp;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class OtpServiceImpl implements OtpService {

    @Autowired(required = false)
    StringRedisTemplate redisTemplate;

    static final String OTP_PREFIX = "OTP_RESET_PW:";
    static final long OTP_VALID_MINUTES = 10;
    static final SecureRandom RANDOM = new SecureRandom();

    // In-memory fallback if Redis is unavailable or not running
    static class OtpEntry {
        final String code;
        final Instant expiresAt;

        OtpEntry(String code, Instant expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    final Map<String, OtpEntry> fallbackCache = new ConcurrentHashMap<>();

    @Override
    public String generateAndStoreOtp(String email) {
        if (email == null || email.isBlank()) return "";
        String normalizedEmail = email.trim().toLowerCase();

        // Sinh mã 6 chữ số ngẫu nhiên
        int number = 100000 + RANDOM.nextInt(900000);
        String otp = String.valueOf(number);

        boolean redisSaved = false;
        if (redisTemplate != null) {
            try {
                redisTemplate.opsForValue().set(OTP_PREFIX + normalizedEmail, otp, Duration.ofMinutes(OTP_VALID_MINUTES));
                redisSaved = true;
            } catch (Exception e) {
                log.warn("Redis unavailable for storing OTP, using in-memory cache fallback: {}", e.getMessage());
            }
        }

        // Always also save to local cache for robustness
        fallbackCache.put(normalizedEmail, new OtpEntry(otp, Instant.now().plus(Duration.ofMinutes(OTP_VALID_MINUTES))));
        log.info("Generated OTP for {}: {} (Redis active: {})", normalizedEmail, otp, redisSaved);
        return otp;
    }

    @Override
    public boolean verifyOtp(String email, String otp) {
        if (email == null || email.isBlank() || otp == null || otp.isBlank()) return false;
        String normalizedEmail = email.trim().toLowerCase();
        String candidateOtp = otp.trim();

        // 1. Check Redis first
        if (redisTemplate != null) {
            try {
                String storedOtp = redisTemplate.opsForValue().get(OTP_PREFIX + normalizedEmail);
                if (storedOtp != null && storedOtp.equals(candidateOtp)) {
                    return true;
                }
            } catch (Exception e) {
                log.warn("Redis read failed during OTP verification: {}", e.getMessage());
            }
        }

        // 2. Check fallback in-memory cache
        OtpEntry entry = fallbackCache.get(normalizedEmail);
        if (entry != null) {
            if (Instant.now().isBefore(entry.expiresAt) && entry.code.equals(candidateOtp)) {
                return true;
            }
            if (Instant.now().isAfter(entry.expiresAt)) {
                fallbackCache.remove(normalizedEmail);
            }
        }

        return false;
    }

    @Override
    public void clearOtp(String email) {
        if (email == null || email.isBlank()) return;
        String normalizedEmail = email.trim().toLowerCase();

        if (redisTemplate != null) {
            try {
                redisTemplate.delete(OTP_PREFIX + normalizedEmail);
            } catch (Exception ignored) {}
        }
        fallbackCache.remove(normalizedEmail);
    }
}
