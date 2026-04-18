package com.sosim.server.jwt.domain.repository;

import java.time.Duration;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class JwtRepository {

    private static final String REDIS_REFRESH_PREFIX = "refresh:";

    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpiration;

    public void saveRefreshToken(Long userId, String deviceId, String refreshToken) {
        String key = REDIS_REFRESH_PREFIX + userId;
        redisTemplate.opsForHash().put(key, deviceId, refreshToken);
        redisTemplate.expire(key, Duration.ofMillis(refreshExpiration));
    }

    public String getRefreshToken(Long userId, String deviceId) {
        return (String) redisTemplate.opsForHash().get(REDIS_REFRESH_PREFIX + userId, deviceId);
    }

    public void deleteRefreshToken(Long userId, String deviceId) {
        redisTemplate.opsForHash().delete(REDIS_REFRESH_PREFIX + userId, deviceId);
    }

    public void deleteAllRefreshTokens(Long userId) {
        redisTemplate.delete(REDIS_REFRESH_PREFIX + userId);
    }

    public Map<Object, Object> getAllDevices(Long userId) {
        return redisTemplate.opsForHash().entries(REDIS_REFRESH_PREFIX + userId);
    }
}
