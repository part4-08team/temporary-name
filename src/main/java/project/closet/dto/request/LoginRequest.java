package project.closet.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "사용자 이름은 필수입니다")
        String email,

        @NotBlank(message = "비밀번호는 필수입니다")
        String password
) {

}
