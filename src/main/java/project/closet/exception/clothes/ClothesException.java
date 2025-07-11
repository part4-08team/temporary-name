package project.closet.exception.clothes;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class ClothesException extends ClosetException {

    protected ClothesException(ErrorCode errorCode) {
        super(errorCode);
    }
    protected ClothesException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }
}
