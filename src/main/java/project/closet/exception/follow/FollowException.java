package project.closet.exception.follow;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class FollowException extends ClosetException {

    public FollowException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FollowException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
