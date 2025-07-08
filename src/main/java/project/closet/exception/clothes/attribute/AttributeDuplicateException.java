package project.closet.exception.clothes.attribute;

import project.closet.exception.ErrorCode;

public class AttributeDuplicateException extends AttributeException {

    /**
     * 원인 예외를 포함하는 생성자
     */
    public AttributeDuplicateException() {
        super(ErrorCode.ATTRIBUTE_DEFINITION_DUPLICATE);
    }
}
