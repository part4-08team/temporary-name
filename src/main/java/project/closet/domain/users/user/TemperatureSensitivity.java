package project.closet.domain.users.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemperatureSensitivity {
  ZERO(0, "추위 많이 탐"),
  ONE(1, "추위에 조금 민감"),
  TWO(2, "추위를 약간 느낌" ),
  THREE(3, "더위를 약간 느낌"),
  FOUR(4, "더위에 조금 민감"),
  FIVE(5, "더위 많이 탐");
  private final int value;
  private final String description;
}
