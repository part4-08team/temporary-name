package project.closet.domain.users;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import project.closet.domain.users.dto.UserCreateRequest;
import project.closet.domain.users.dto.UserDto;
import project.closet.domain.users.repository.ProfileRepository;
import project.closet.domain.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private ProfileRepository profileRepository;

  @InjectMocks
  private UserServiceImpl userService;

  @BeforeEach
  void setUp() {

  }

  @Test
  @DisplayName("회원가입_성공")
  void registerUser_success() {
    // given
    var request = new UserCreateRequest("register-name", "register-email",
        "register-password");

    when(userRepository.existsByEmail(request.email())).thenReturn(false);
    when(passwordEncoder.encode(request.password())).thenReturn("encoded-password");
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      ReflectionTestUtils.setField(user, "id", UUID.randomUUID());
      ReflectionTestUtils.setField(user, "createdAt", Instant.now());
      ReflectionTestUtils.setField(user, "updatedAt", Instant.now());
      return user;
    });

    // when
    UserDto userDto = userService.registerUser(request);

    verify(userRepository, times(1)).existsByEmail(request.email());
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordEncoder, times(1)).encode(request.password());
    assertThat(userDto).isNotNull();
    assertThat(userDto.id()).isNotNull();
    assertThat(userDto.email()).isEqualTo(request.email());
    assertThat(userDto.name()).isEqualTo(request.name());
  }

  @Test
  @DisplayName("회원가입 실패 - 중복된 이메일")
  void registerUser_fail_duplicatedEmail() {
    // given
    var request = new UserCreateRequest("register-name", "register-email",
        "register-password");

    when(userRepository.existsByEmail(request.email())).thenReturn(true);

    // when
    assertThatThrownBy(() -> userService.registerUser(request))
        .isInstanceOf(Exception.class); // todo : 예외 수정
  }



}