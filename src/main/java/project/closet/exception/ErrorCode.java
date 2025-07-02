package project.closet.exception;
import lombok.Getter;

@Getter
public enum ErrorCode {
    //DM
    DM_NOT_FOUND("DM을 찾을 수 없습니다."),
    //FEED
    FEED_NOT_FOUND("피드를 찾을 수 없습니다."),

    INVALID_REQUEST("잘못된 요청입니다."),
    INTERNAL_ERROR("서버 내부 오류입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
