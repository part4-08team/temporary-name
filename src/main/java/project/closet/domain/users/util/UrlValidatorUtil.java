package project.closet.domain.users.util;

import org.apache.commons.validator.routines.UrlValidator;

public class UrlValidatorUtil {
  private static final UrlValidator urlValidator = new UrlValidator();
  public static boolean isValidUrl(String url) {
    return urlValidator.isValid(url);
  }
}
