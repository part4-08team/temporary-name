package project.closet.domain.users;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import project.closet.config.TestSecurityConfig;
import project.closet.domain.users.Profile.TemperatureSensitivity;
import project.closet.domain.users.User.Gender;
import project.closet.domain.users.User.UserRole;
import project.closet.domain.users.dto.ChangePasswordRequest;
import project.closet.domain.users.dto.ProfileDto;
import project.closet.domain.users.dto.ProfileUpdateRequest;
import project.closet.domain.users.dto.ProfileUpdateWithImageUrlRequest;
import project.closet.domain.users.dto.UserCreateRequest;
import project.closet.domain.users.dto.UserDto;
import project.closet.domain.users.dto.UserLockUpdateRequest;
import project.closet.domain.users.dto.UserRoleUpdateRequest;

//@Import(UserServiceImpl.class) //mocking 용
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

  final UUID id = UUID.randomUUID();
  final String name = "new username";
  final String email = "example@gmail.com";
  final String password = "new password";
  final LocalDate birthDate = LocalDate.of(1999, 10, 24);

  @Autowired
  MockMvcTester mockMvcTester;

  @Autowired
  ObjectMapper objectMapper;

  @MockitoBean
  UserService userService;

  @Test
  void createUser() throws JsonProcessingException {

    var newUser = new UserCreateRequest(name, email, password);
    LocalDateTime now = LocalDateTime.now();

    when(userService.registerUser(newUser))
        .thenAnswer(invocationOnMock -> {
          return new UserDto(id, now, email, name, UserRole.USER, new HashSet<>(), false);
        });

    var result = mockMvcTester.post()
        .uri("/api/users")
        //.with(csrf()) // CSRF 토큰 추가 (TestConfig에 놓았으면 필요 없음)
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(newUser))
        .assertThat().apply(print())
        .hasStatusOk().bodyJson();

    result.extractingPath("$.id").isEqualTo(id.toString());
    result.extractingPath("$.email").isEqualTo(email);
    result.extractingPath("$.name").isEqualTo(name);
    result.extractingPath("$.role").isEqualTo(UserRole.USER.toString());
    result.extractingPath("$.locked").isEqualTo(false);
  }

  @WithMockUser(roles = "ADMIN") // 인증된 사용자로 테스트
  @Test
  void updateUserRole_success() throws JsonProcessingException {
    var request = new UserRoleUpdateRequest(UserRole.ADMIN);
    UUID userId = UUID.randomUUID();

    when(userService.updateUserRole(userId, request))
        .thenAnswer(invocationOnMock ->
            new UserDto(userId, LocalDateTime.now(), email, name, UserRole.ADMIN, new HashSet<>(),
                false));

    var result = mockMvcTester.patch()
        .uri("/api/users/" + userId + "/role")
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(request))
        .assertThat()
        .hasStatusOk()
        .bodyJson();

    result.extractingPath("$.id").isEqualTo(userId.toString());
    result.extractingPath("$.email").isEqualTo(email);
    result.extractingPath("$.name").isEqualTo(name);
    result.extractingPath("$.role").isEqualTo(UserRole.ADMIN.toString());
    result.extractingPath("$.linkedOAuthProviders").asArray().hasSize(0);
    result.extractingPath("$.locked").isEqualTo(false);
  }

  @WithMockUser
  @Test
  void getUserProfiles_success() throws JsonProcessingException {
    TemperatureSensitivity sensitivity = TemperatureSensitivity.TWO;

    when(userService.getUserProfile(id))
        .thenAnswer(invocationOnMock ->
            new ProfileDto(id, name, Gender.MALE, birthDate, null, sensitivity, null)
        );

    var result = mockMvcTester.get()
        .uri("/api/users/" + id + "/profiles")
        .assertThat()
        .hasStatusOk()
        .bodyJson();

    result.extractingPath("$.userId").isEqualTo(id.toString());
    result.extractingPath("$.name").isEqualTo(name);
    result.extractingPath("$.gender").isEqualTo(Gender.MALE.toString());
    result.extractingPath("$.birthDate").isEqualTo(birthDate.toString());
    result.extractingPath("$.temperatureSensitivity").isEqualTo(sensitivity.toString());
  }

  @WithMockUser
  @Test
  void updateUserProfile_success() throws JsonProcessingException {
    String imageUrl = "http://example.com/image.png";
    var profileUpdateRequest = new ProfileUpdateRequest(name, Gender.MALE,
        birthDate, null, TemperatureSensitivity.TWO);
    var request = new ProfileUpdateWithImageUrlRequest(profileUpdateRequest, imageUrl);

    when(userService.updateUserProfile(id, request))
        .thenAnswer(invocationOnMock ->
            new ProfileDto(id, name, Gender.MALE, birthDate, null, TemperatureSensitivity.TWO,
                imageUrl)
        );

    var result = mockMvcTester.patch()
        .uri("/api/users/" + id + "/profiles")
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(request))
        .assertThat().hasStatusOk()
        .bodyJson();

    result.extractingPath("$.userId").isEqualTo(id.toString());
    result.extractingPath("$.name").isEqualTo(name);
    result.extractingPath("$.gender").isEqualTo(Gender.MALE.toString());
    result.extractingPath("$.birthDate").isEqualTo(birthDate.toString());
    result.extractingPath("$.temperatureSensitivity")
        .isEqualTo(TemperatureSensitivity.TWO.toString());
    result.extractingPath("$.profileImageUrl").isEqualTo(imageUrl);
  }

  @Test
  @DisplayName("올바르지 않은 profile Image Url이면 @Valid 예외")
  void wrongImageUrl_failure() throws JsonProcessingException {
    String wrongImageUrl = "wrongImageUrl";

    var profileUpdateRequest = new ProfileUpdateRequest(name, Gender.MALE,
        birthDate, null, TemperatureSensitivity.TWO);

    assertThatThrownBy(() ->
        new ProfileUpdateWithImageUrlRequest(profileUpdateRequest, wrongImageUrl));
  }

  @WithMockUser
  @Test
  void updateUserPassword_success() throws JsonProcessingException {
    var request = new ChangePasswordRequest("new password");
    mockMvcTester.patch()
        .uri("/api/users/" + id + "/password")
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(request))
        .assertThat()
        .hasStatusOk().bodyJson();
  }

  @WithMockUser(roles = "ADMIN")
  @Test
  void lockUser_success() throws JsonProcessingException {
    var request = new UserLockUpdateRequest(true);

    when(userService.updateUserLock(id, request)).thenReturn(id);

    var result = mockMvcTester.patch()
        .uri("/api/users/" + id + "/lock")
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(request))
        .assertThat()
        .hasStatusOk().bodyJson();

    result.extractingPath("$").isEqualTo(id.toString());

  }
}
