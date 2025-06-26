package project.closet.domain.users.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TemperatureSensitivity {
  ONE(1, "추위 민감"),
  TWO(2, "추위 조금 민감" ),
  THREE(3, "보통"),
  FOUR(4, "더위 조금 민감"),
  FIVE(5, "더위 민감");
  private final int value;
  private final String description;
}
