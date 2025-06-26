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
  private Gender gender;

  @Column(name = "birth_date")
  private LocalDate birthDate;

  @Column(name = "profile_image_url")
  private String profileImgUrl;

  @Column(name = "location", columnDefinition = "location_type")
  private Location location;

  @Column(name = "temperature_sensitivity", nullable = false, columnDefinition = "temperature_sensitivity_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private TemperatureSensitivity temperatureSensitivity = TemperatureSensitivity.ZERO;

  protected Profile(User user, String name) {
    this.user = Objects.requireNonNull(user, "user must not be null");
    this.name = Objects.requireNonNull(name, "name must not be null");
  }

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

  // toString
}
