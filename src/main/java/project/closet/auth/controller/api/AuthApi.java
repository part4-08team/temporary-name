package project.closet.auth.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import project.closet.dto.request.ResetPasswordRequest;
import project.closet.exception.ErrorResponse;

@Tag(name = "인증 관리", description = "인증 관련 API")
public interface AuthApi {

    @Operation(summary = "CSRF 토큰 발급")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "발급 성공",
                    content = @Content(schema = @Schema(implementation = CsrfToken.class))
            )
    })
    ResponseEntity<CsrfToken> getCsrfToken(@Parameter(hidden = true) CsrfToken csrfToken);

    @Operation(summary = "리프레시 토큰을 활용한 엑세스 토큰 조회")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "조회 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<String> me(@Parameter(hidden = true) String refreshToken);

    @Operation(summary = "리프레시 토큰을 활용한 엑세스 토큰 재발급")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "재발급 성공",
                    content = @Content(schema = @Schema(implementation = String.class))
            ),
            @ApiResponse(
                    responseCode = "401", description = "유효하지 않은 리프레시 토큰",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<String> refresh(
            @Parameter(hidden = true) String refreshToken,
            @Parameter(hidden = true) HttpServletResponse response
    );

    // 비밀번호 초기화
    @Operation(summary = "비밀번호 초기화", description = "임시 비밀번호로 초기화 후 이메일로 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204", description = "비밀번호 초기화 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "잘못된 요청",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<Void> resetPassword(ResetPasswordRequest resetPasswordRequest);
}
