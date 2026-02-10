package org.example.bookingservice.Service;

import lombok.RequiredArgsConstructor;
import org.example.bookingservice.Entity.BookingEntity;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UtilService {
    private final StringRedisTemplate redisTemplate;
    private final RedisTemplate<String, BookingEntity> bookingRedisTemplate;


    private static final long TTL_SECONDS = 600000; // 10 minutes

    public boolean createKey(String key) {

        System.out.println("🔥🔥🔥 REDIS createKey CALLED with key = " + key);

        Boolean result = redisTemplate.opsForValue()
                .setIfAbsent(key, "CREATED", Duration.ofSeconds(TTL_SECONDS));

        System.out.println("🔥🔥🔥 REDIS SET RESULT = " + result);

        return Boolean.TRUE.equals(result);
    }

    public void updateStatus(String key, String status) {
        redisTemplate.opsForValue()
                .set(key, status, Duration.ofSeconds(TTL_SECONDS));
    }

    public void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    public String getStatus(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    //BOOKIing Entity

    public void saveBooking(String idempotencyKey, BookingEntity booking) {
        bookingRedisTemplate.opsForValue().set(
                "booking:" + idempotencyKey,
                booking,
                Duration.ofMinutes(10)
        );
    }
    public BookingEntity getBooking(String idempotencyKey) {
        return bookingRedisTemplate.opsForValue()
                .get("booking:" + idempotencyKey);
    }
    public void deleteBooking(String idempotencyKey) {
        bookingRedisTemplate.delete("booking:" + idempotencyKey);
    }




}
