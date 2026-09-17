package com.toan.university_management.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.URI;
import java.time.Duration;

@Slf4j
@Configuration
public class RedisConfig implements CachingConfigurer {

    @Value("${spring.data.redis.url:${REDIS_URL:}}")
    private String redisUrl;

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.username:${REDIS_USERNAME:default}}")
    private String redisUsername;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Value("${spring.data.redis.ssl.enabled:false}")
    private boolean redisSslEnabled;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        // 1. Ưu tiên phân giải nếu được cung cấp chuỗi URL (Ví dụ Upstash: rediss://default:xxx@xxx.upstash.io:6379)
        if (redisUrl != null && !redisUrl.isBlank()) {
            try {
                URI uri = URI.create(redisUrl.trim());
                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 6379;
                boolean isSsl = "rediss".equalsIgnoreCase(uri.getScheme()) || (host != null && host.endsWith(".upstash.io"));

                RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
                if (uri.getUserInfo() != null && !uri.getUserInfo().isBlank()) {
                    String[] parts = uri.getUserInfo().split(":", 2);
                    if (parts.length == 2) {
                        if (!parts[0].isBlank()) {
                            config.setUsername(parts[0]);
                        }
                        config.setPassword(RedisPassword.of(parts[1]));
                    } else {
                        config.setPassword(RedisPassword.of(parts[0]));
                    }
                }

                LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder()
                        .commandTimeout(Duration.ofSeconds(10));
                if (isSsl) {
                    builder.useSsl();
                }

                log.info("Redis configured via REDIS_URL -> Host: {}, Port: {}, SSL: {}", host, port, isSsl);
                return new LettuceConnectionFactory(config, builder.build());
            } catch (Exception ex) {
                log.warn("Failed to parse REDIS_URL [{}]: {}. Falling back to individual host/port properties.", redisUrl, ex.getMessage());
            }
        }

        // 2. Phân giải theo từng tham số riêng lẻ (Host, Port, Password, SSL)
        String cleanHost = (redisHost != null) ? redisHost.trim() : "localhost";
        boolean useSsl = redisSslEnabled;

        // Tự động làm sạch nếu người dùng lỡ paste cả rediss:// hoặc redis:// vào HOST
        if (cleanHost.startsWith("rediss://")) {
            cleanHost = cleanHost.substring(9);
            useSsl = true;
        } else if (cleanHost.startsWith("redis://")) {
            cleanHost = cleanHost.substring(8);
        }

        // Nếu host là Upstash Cloud (*.upstash.io), Upstash BẮT BUỘC dùng SSL
        if (cleanHost.endsWith(".upstash.io")) {
            useSsl = true;
        }

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(cleanHost, redisPort);
        if (redisUsername != null && !redisUsername.isBlank()) {
            config.setUsername(redisUsername);
        }
        if (redisPassword != null && !redisPassword.isBlank()) {
            config.setPassword(RedisPassword.of(redisPassword));
        }

        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(10));
        if (useSsl) {
            builder.useSsl();
        }

        log.info("Redis configured -> Host: {}, Port: {}, SSL: {}", cleanHost, redisPort, useSsl);
        return new LettuceConnectionFactory(config, builder.build());
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis CacheGet error on key {}: {}", key, exception.getMessage());
            }

            @Override
            public void handleCachePutError(RuntimeException exception, org.springframework.cache.Cache cache, Object key, Object value) {
                log.warn("Redis CachePut error on key {}: {}", key, exception.getMessage());
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, org.springframework.cache.Cache cache, Object key) {
                log.warn("Redis CacheEvict error on key {}: {}", key, exception.getMessage());
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, org.springframework.cache.Cache cache) {
                log.warn("Redis CacheClear error: {}", exception.getMessage());
            }
        };
    }
}

