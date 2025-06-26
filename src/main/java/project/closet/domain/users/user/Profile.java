package project.closet.domain.users.user;

import static jakarta.persistence.FetchType.LAZY;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import project.closet.common.dto.Location;
import project.closet.domain.base.BaseUpdatableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "profiles")
public class Profile extends BaseUpdatableEntity {

  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "profile_id", nullable = false, updatable = false, unique = true)
  private UUID id;

  @OneToOne(fetch = LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "gender", columnDefinition = "gender_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private User.Gender gender;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "profile_image_url")
  private String profileImageUrl;

  @Column(name = "location", columnDefinition = "location_type")
  private Location location;

  @Column(name = "temperature_sensitivity", nullable = false, columnDefinition = "temperature_sensitivity_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private TemperatureSensitivity temperatureSensitivity = TemperatureSensitivity.ZERO;

  Profile(User user, String name) {
    this.user = Objects.requireNonNull(user, "user must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

  // update 추가

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Profile profile = (Profile) o;
    return Objects.equals(id, profile.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  @Override
  public String toString() {
    return "Profile{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", gender=" + gender +
        ", birthDate=" + birthDate +
        ", temperatureSensitivity=" + temperatureSensitivity +
        '}';
  }

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
}
