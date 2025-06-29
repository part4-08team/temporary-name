package project.closet.global.config.redis;


import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RedisRepository {

  private final RedisTemplate<String, String> redisTemplate;
  private static final String KEY_PREFIX = "user:";

  // todo : 어떤 형태로 저장할지 고민
  public void save(UUID userId, Duration ttl) {
    String key = KEY_PREFIX + userId;
    redisTemplate.opsForValue().set(key, ttl.toString(), ttl);
  }

  public String findByUserId(UUID userId) {
    String key = KEY_PREFIX + userId;
    return redisTemplate.opsForValue().get(key);
  }

  public void deleteByUserId(UUID userId) {
    String key = KEY_PREFIX + userId;
    redisTemplate.delete(key);
  }

  public boolean existsByUserId(UUID userId) {
    String key = KEY_PREFIX + userId;
    return redisTemplate.hasKey(key);
  }
}
