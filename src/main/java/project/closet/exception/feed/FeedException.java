package project.closet.exception.feed;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class FeedException extends ClosetException {

    public FeedException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FeedException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
