package project.closet.domain.users.util;


import org.apache.commons.lang3.RandomStringUtils;

public abstract class TemporaryPasswordFactory {

  public static String createTempPassword() {
    return RandomStringUtils.secure().next(16, 0, 0, true, true);
  }
}
