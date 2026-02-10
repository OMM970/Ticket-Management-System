package org.example.bookingservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LockService {
    private final RedisTemplate<String, String> redisTemplate;

    public boolean acquireLock(
            String key,
            String value,
            int ttlSeconds
    ) {
        Boolean locked = redisTemplate.opsForValue()
                .setIfAbsent(key, value, Duration.ofSeconds(ttlSeconds));
        return Boolean.TRUE.equals(locked);
    }

    public void releaseLock(String key, String value) {
        String currentValue = redisTemplate.opsForValue().get(key);
        if (value.equals(currentValue)) {
            redisTemplate.delete(key);
        }
    }

}
