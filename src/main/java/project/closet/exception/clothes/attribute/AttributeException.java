package project.closet.exception.clothes.attribute;

import project.closet.exception.ClosetException;
import project.closet.exception.ErrorCode;

public class AttributeException extends ClosetException {

    protected AttributeException(ErrorCode errorCode) {super(errorCode);}

    protected AttributeException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
    }

}
