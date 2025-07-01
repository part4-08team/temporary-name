package project.closet.domain.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import project.closet.domain.users.User.UserRole;

class UserTest {


  User user = User.createUserWithProfile("John", "test@gmail.com", "password");

  @BeforeEach
  void setUp() {
    user = User.createUserWithProfile("John", "test@gmail.com", "password");
  }


  @Test
  @DisplayName("사용자 + profile 생성")
  void createUserWithProfile() {
    // given
    String name = "John";
    String email = "example@naver.com";
    String hashedPassword = "hashedPassword";

    // when
    User user = User.createUserWithProfile(name, email, hashedPassword);
    Profile profile = user.getProfile();

    // then
    assertThat(user).isNotNull();
    assertThat(user.getEmail()).isEqualTo(email);
    assertThat(user.getPassword()).isEqualTo(hashedPassword);
    assertThat(user.getRole()).isEqualTo(UserRole.USER);
    assertThat(user.isLocked()).isFalse();
    assertThat(user.isTemporaryPassword()).isFalse();
    assertThat(profile).isNotNull();
    assertThat(profile.getName()).isEqualTo(name);
  }

  @Test
  @DisplayName("name이 null인 경우 예외 발생 when createUserWithProfile")
  void createUserWithProfile_nullName_throwsException() {

    // given
    String name = null;
    String email = "test@email.com";
    String password = "password";

    // when & then
    assertThatThrownBy(() -> User.createUserWithProfile(name, email, password))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("name must not be null");
  }

  @Test
  @DisplayName("email이 null인 경우 예외 발생")
  void createUserWithProfile_nullEmail_throwsException() {
    // given
    String name = "John";
    String email = null;
    String password = "password";

    // when & then
    assertThatThrownBy(() -> User.createUserWithProfile(name, email, password))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("email must not be null");
  }

  @Test
  @DisplayName("password가 null인 경우 예외 발생")
  void createUserWithProfile_nullPassword_throwsException() {
    // given
    String name = "John";
    String email = "test@email.com";
    String password = null;

    // when & then
    assertThatThrownBy(() -> User.createUserWithProfile(name, email, password))
        .isInstanceOf(NullPointerException.class)
        .hasMessage("password must not be null");
  }

  @Test
  @DisplayName("역할 변경 성공")
  void changeRole_Success() {
    // given
    UserRole newRole = UserRole.ADMIN;

    // when
    user.changeRole(newRole);

    // then
    assertThat(user.getRole()).isEqualTo(newRole);
  }

  @Test
  @DisplayName("역할 변경 실패 - 파라미터로 null 전달 시 예외 발생")
  void changeRole_Fail() {
    // given
    UserRole newRole = null;

    // when, then
    assertThatThrownBy(() -> user.changeRole(newRole))
        .isInstanceOf(NullPointerException.class);
  }


  @Test
  @DisplayName("비밀번호 변경 성공")
  void changePassword_success() {
    // given
    String newPassword = "newPassword";

    // when
    user.changePassword(newPassword);

    // then
    assertThat(user.getPassword()).isEqualTo(newPassword);
  }

  @Test
  @DisplayName("null 비밀번호로 변경 시 예외 발생")
  void changePassword_nullPassword_throwsException() {
    // when & then
    assertThatThrownBy(() -> user.changePassword(null))
        .isInstanceOf(NullPointerException.class);
  }

  @Test
  @DisplayName("계정 잠금 상태 변경 성공")
  void changeLocked_success() {

    // 초기값 확인
    assertThat(user.isLocked()).isFalse();

    // when
    user.changeLocked(true);

    // then
    assertThat(user.isLocked()).isTrue();
  }

  @Test
  @DisplayName("잠금 해제 성공")
  void changeLocked_unlock_success() {
    // given
    user.changeLocked(true);

    // when
    user.changeLocked(false);

    // then
    assertThat(user.isLocked()).isFalse();
  }
}
