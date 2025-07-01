package project.closet.domain.users.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    String password) {
}
