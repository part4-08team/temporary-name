package project.closet.domain.users.util;

import org.apache.commons.validator.routines.UrlValidator;

public abstract class UrlValidatorUtil {
  private static final UrlValidator URL_VALIDATOR = new UrlValidator();
  public static boolean isValidUrl(String url) {
    return URL_VALIDATOR.isValid(url);
  }
}
