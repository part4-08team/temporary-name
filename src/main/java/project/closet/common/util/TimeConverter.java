package project.closet.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public abstract class TimeConverter {

  public static LocalDateTime toLocalDateTime(Instant instant) {
    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
  }

  public static Instant toInstant(LocalDateTime localDateTime) {
    return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
  }
}
