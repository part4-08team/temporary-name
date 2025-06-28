package project.closet.domain.users;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import project.closet.domain.base.BaseUpdatableEntity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User extends BaseUpdatableEntity {

  @Id @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "user_id", nullable = false, updatable = false, unique = true)
  private UUID id;

  @Column(name = "password")
  private String password;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "locked", nullable = false)
  private boolean locked = false;

  @Column(name = "role", nullable = false, columnDefinition = "user_role_type")
  @Enumerated(EnumType.STRING)
  @JdbcType(PostgreSQLEnumJdbcType.class)
  private UserRole role = UserRole.USER;

  @Column(name = "is_temporary_password", nullable = false)
  private boolean isTemporaryPassword = false;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Profile profile;

  private User(String email, String password) {
    this.password = password;
    this.email = email;
  }

  static User createUserWithProfile(String name, String email, String password) {
    Objects.requireNonNull(name, "name must not be null");
    Objects.requireNonNull(email, "email must not be null");
    Objects.requireNonNull(password, "password must not be null");

    User createdUser = new User(email, password);
    createdUser.profile = new Profile(createdUser, name);
    return createdUser;
  }

  void changeRole(UserRole userRole) {
    if (userRole == role) return;
    Objects.requireNonNull(userRole, "userRole must not be null");
    this.role = userRole;
  }

  void changePassword(String password) {
    Objects.requireNonNull(password, "password must not be null");
    this.password = password;
  }

  void changeLocked(boolean locked) {
    if (this.locked == locked) return;
    this.locked = locked;
  }

  void changeTemporaryPassword(boolean isTemporaryPassword) {
    if (this.isTemporaryPassword == isTemporaryPassword) return;
    this.isTemporaryPassword = isTemporaryPassword;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    User user = (User) o;
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }

  public enum UserRole {
    USER, ADMIN
  }

  public enum Gender {
    MALE, FEMALE, OTHER
  }

  @Override
  public String toString() {
    return "User{" +
        "id=" + id +
        ", email='" + email + '\'' +
        ", locked=" + locked +
        ", role=" + role +
        ", isTemporaryPassword=" + isTemporaryPassword + '}';
  }
}
