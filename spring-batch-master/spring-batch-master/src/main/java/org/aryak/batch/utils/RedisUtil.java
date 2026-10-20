package org.aryak.batch.utils;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class RedisUtil {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * fetches the value from redis
     *
     * @param filename
     * @return last processed line number
     */
    public int getLastProcessedCount(String filename) {

        int lastRead = 0;
        try {
            String v = redisTemplate.opsForValue().get(filename);
            if (v != null) {
                lastRead = Integer.parseInt(v);
            }
        } catch (Exception ignored) {
        }
        log.info("Last read/ skip count computed as : {}", lastRead);
        return lastRead;
    }

    public void updateOffset(String filename, int i) {
        redisTemplate.opsForValue().set(filename, String.valueOf(i));
    }
}
