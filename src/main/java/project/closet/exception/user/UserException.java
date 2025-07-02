package project.closet.exception.user;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class UserException extends ClosetException {

    protected UserException(ErrorCode errorCode) {
        super(errorCode);
    }

    protected UserException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
