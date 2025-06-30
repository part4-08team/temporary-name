package project.closet.domain.users;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import project.closet.domain.users.User.UserRole;
import project.closet.domain.users.dto.UserCreateRequest;
import project.closet.domain.users.dto.UserDto;
import project.closet.domain.users.dto.UserRoleUpdateRequest;

//@Import(UserServiceImpl.class) //mocking 용
@WebMvcTest(UserController.class)
@Import(TestSecurityConfig.class)
public class UserControllerTest {

  final String name = "new username";
  final String email = "example@gmail.com";
  final String password = "new password";

  @Autowired
  MockMvcTester mockMvcTester; // 최신 버전

  @Autowired
  ObjectMapper objectMapper; // JSON 처리를 위한 필수

  @MockitoBean  // 이게 최신버전 (deprecated : MockBean)
  UserService userService;  // 서비스 레이어 Mcok 처리

  @Test
  void createUser() throws JsonProcessingException {

    var newUser = new UserCreateRequest(name, email, password);
    UUID id = UUID.randomUUID();
    LocalDateTime now = LocalDateTime.now();

    when(userService.registerUser(newUser))
        .thenAnswer( invocationOnMock -> {
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
  void updateUserRole() throws JsonProcessingException {
    var request = new UserRoleUpdateRequest(UserRole.ADMIN);
    UUID userId = UUID.randomUUID();

    when(userService.updateUserRole(userId, request))
        .thenAnswer( invocationOnMock ->
            new UserDto(userId, LocalDateTime.now(), email, name, UserRole.ADMIN, new HashSet<>(), false));

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
  }
}
