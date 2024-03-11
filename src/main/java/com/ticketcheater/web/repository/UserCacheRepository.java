package com.ticketcheater.web.repository;

import com.ticketcheater.web.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@Log4j2
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, UserDTO> userRedisTemplate;

    private static final Duration USER_CACHE_TTL = Duration.ofDays(1);

    public void setUser(UserDTO user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}({})", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    public Optional<UserDTO> getUser(String username) {
        UserDTO data = userRedisTemplate.opsForValue().get(getKey(username));
        log.info("Get User from Redis {}", data);
        return Optional.ofNullable(data);
    }

    private String getKey(String username) {
        return "USERDTO:" + username;
    }

}
