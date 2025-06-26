package project.closet.domain.user.auth.dto;

import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;

public record ChangePasswordRequest(
    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    String password) {
}
