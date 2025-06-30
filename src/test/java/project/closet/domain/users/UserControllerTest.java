package project.closet.domain.users;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import project.closet.domain.users.dto.UserCreateRequest;

//@Import(UserServiceImpl.class) //mocking 용
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  MockMvcTester mockMvcTester; // 최신 버전

  @Autowired
  ObjectMapper objectMapper; // JSON 처리를 위한 필수

  @MockitoBean  // 이게 최신버전 (deprecated : MockBean)
  UserService userService;  // 서비스 레이어 Mcok 처리

  @Test
  void createUser() throws JsonProcessingException {
    var newUser = new UserCreateRequest(
        "new username",
        "new email",
        "new password"
    );

    mockMvcTester.post()
        .uri("/api/users")
        .contentType(String.valueOf(MediaType.APPLICATION_JSON))
        .content(objectMapper.writeValueAsString(newUser))
        .assertThat().apply(print());
  }



}
