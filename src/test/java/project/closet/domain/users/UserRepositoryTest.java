package project.closet.domain.users;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import project.closet.domain.users.repository.ProfileRepository;
import project.closet.domain.users.repository.UserRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private ProfileRepository profileRepository;

  @Autowired
  private TestEntityManager tem;

  @Test
  @DisplayName("user와 profile이 함께 save")
  void saveUserWithProfile() {
    // given
    var name = "John";
    var email = "Q1YrQ@example.com";
    var password = "password";

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
}
