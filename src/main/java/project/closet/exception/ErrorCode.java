package project.closet.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    //DM
    DM_NOT_FOUND("DM을 찾을 수 없습니다."),
    //FEED
    FEED_NOT_FOUND("피드를 찾을 수 없습니다."),
    FEED_ALREADY_LIKE_EXISTS("이미 피드에 좋아요를 했습니다."),
    // USER
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    DUPLICATE_USER("이미 존재하는 사용자입니다."),

    // FOLLOW
    SELF_FOLLOW_NOT_ALLOWED("자기 자신을 팔로우할 수 없습니다."),
    FOLLOW_NOT_FOUND("팔로우를 찾을 수 없습니다."),

    // WEATHER
    WEATHER_NOT_FOUND("날씨 정보를 찾을 수 없습니다."),

    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_ERROR("서버 내부 오류입니다."),

    // 인증/인가 에러 코드
    INVALID_TOKEN_SECRET("유효하지 않은 시크릿입니다."),
    INVALID_TOKEN("유효하지 않은 토큰입니다."),
    TOKEN_NOT_FOUND("인증 정보가 없습니다. 다시 로그인해주세요."),

    //의상 속성 정의 에러 코드
    ATTRIBUTE_DEFINITION_NOT_FOUND("해당 속성 정의를 찾을 수 없습니다."),
    ATTRIBUTE_DEFINITION_DUPLICATE("이미 존재하는 속성 정의 이름입니다."),

    //의상 에러 코드
    CLOTHES_NOT_FOUND("의상을 찾을 수 없습니다."),
    EXTRACTION_FAILED("상품 정보 추출에 실패했습니다."),
    UNSUPPORTED_SHOP("지원하지 않는 쇼핑몰입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
