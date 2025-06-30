package project.closet.global.config.security;

import lombok.Getter;

// Front cookie name = refresh_token
@Getter
public enum TokenType {
  ACCESS("access_token"), REFRESH("refresh_token");

  private final String tokenName;

  TokenType(String tokenType) {
    this.tokenName = tokenType;
  }
}
