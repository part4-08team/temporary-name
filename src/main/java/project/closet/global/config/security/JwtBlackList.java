package project.closet.global.config.security;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JwtBlackList {

  private final Map<UUID, Instant> blackList = new ConcurrentHashMap<>();

  public void put(UUID userId, Instant expiredAt) {
    blackList.put(userId, expiredAt);
  }

  public boolean isBlackListed(UUID userId) {
    return blackList.containsKey(userId);
  }

  @Scheduled(fixedDelay = 60 * 60 * 1000)
  public void clear() {
    Instant now = Instant.now();
    blackList.values().removeIf(instant -> instant.isBefore(now));
  }
}
