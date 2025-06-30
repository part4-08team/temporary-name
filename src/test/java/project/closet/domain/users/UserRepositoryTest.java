package project.closet.domain.users;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import project.closet.config.TestContainerConfig;
import project.closet.domain.users.repository.ProfileRepository;
import project.closet.domain.users.repository.UserRepository;
import project.closet.global.config.JpaAuditingConfiguration;

@DataJpaTest
@Testcontainers
@Import({JpaAuditingConfiguration.class, TestContainerConfig.class})
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private static PostgreSQLContainer<?> postgres;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private TestEntityManager tem;

  final String name = "John";
  final String email = "Q1YrQ@example.com";
  final String password = "password";

  @Test
  @DisplayName("user와 profile이 함께 save")
  @Transactional
  void saveUserWithProfile() {
    // given

    // when
    var user = User.createUserWithProfile(name, email, password);
    User savedUser = userRepository.save(user);
    UUID userId = savedUser.getId();

    tem.flush();
    tem.clear();

    // then
    Optional<User> optionalUser = userRepository.findById(userId);
    Optional<Profile> optionalProfile = profileRepository.findByUserId(userId);

    assertThat(optionalUser.isPresent()).isTrue();
    assertThat(optionalProfile.isPresent()).isTrue();

    var foundUser = optionalUser.get();
    var foundProfile = optionalProfile.get();

    assertThat(foundUser).isEqualTo(savedUser);
    assertThat(foundProfile.getUser()).isEqualTo(foundUser);
  }

  @Test
  void findUserByEmail() {
    // given
    User user = User.createUserWithProfile("John", email, "password");
    User savedUser = userRepository.save(user);

    tem.flush();
    tem.clear();

    // when + then
    Optional<User> optionalUser = userRepository.findByEmail(email);
    assertThat(optionalUser.isPresent()).isTrue();

    User foundUser = optionalUser.get();
    assertThat(foundUser).isEqualTo(savedUser);
  }

  @Test
  @DisplayName("올바른 이메일이면 false")
  void existsByEmail_success() {
    // given
    User user = User.createUserWithProfile("John", email, "password");
    User savedUser = userRepository.save(user);

    tem.flush();
    tem.clear();

    // when + then
    boolean exists = userRepository.existsByEmail(email);
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("잘못된 이메일이면 false")
  void existsByEmail_false() {
    String wrongEmail = "wrong-email";
    User user = User.createUserWithProfile("John", email, "password");
    User savedUser = userRepository.save(user);

    tem.flush();
    tem.clear();

    // when + then
    boolean exists = userRepository.existsByEmail(wrongEmail);
    assertThat(exists).isFalse();
  }

  @Test
  void findUserById() {
    // given
    User user = User.createUserWithProfile("John", email, "password");
    User savedUser = userRepository.save(user);

    tem.flush();
    tem.clear();

    // when + then
    Optional<User> optionalUser = userRepository.findById(savedUser.getId());
    assertThat(optionalUser.isPresent()).isTrue();


    User foundUser = optionalUser.get();
    assertThat(foundUser).isEqualTo(savedUser);
  }
}