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
  private static final String KEY_PREFIX = "user:refreshToken:";

  // todo : 어떤 형태로 저장할지 고민
  public void save(UUID userId, String refreshToken, Duration ttl) {
    redisTemplate.opsForValue().set(getKeyName(userId), refreshToken, ttl);
  }

  public String findByUserId(UUID userId) {
    return redisTemplate.opsForValue().get(getKeyName(userId));
  }

  public void deleteByUserId(UUID userId) {
    redisTemplate.delete(getKeyName(userId));
  }

  public boolean existsByUserId(UUID userId) {
    return redisTemplate.hasKey(getKeyName(userId));
  }

  private String getKeyName(UUID userId) {
    return KEY_PREFIX + userId;
  }
}
