package com.ticketcheater.web.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Log4j2
@Repository
@RequiredArgsConstructor
public class TokenCacheRepository {

    private final RedisTemplate<String, String> redisTemplate;

    public void setToken(String username, String token, long timeMs) {
        log.info("Set Refresh Token to Redis {}({})", token, username);
        redisTemplate.opsForValue().set(username, token, timeMs, TimeUnit.MILLISECONDS);
    }

    public String getToken(String username) {
        String refreshToken = redisTemplate.opsForValue().get(username);
        log.info("Get Refresh Token to Redis {}({})", refreshToken, username);
        return refreshToken;
    }

    public void deleteToken(String username) {
        log.info("Delete Refresh Token to Redis ({})", username);
        redisTemplate.opsForValue().getAndDelete(username);
    }

}
