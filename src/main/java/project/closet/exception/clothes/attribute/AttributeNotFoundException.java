package project.closet.exception.clothes.attribute;

import project.closet.exception.ErrorCode;

public class AttributeNotFoundException extends AttributeException {
  public AttributeNotFoundException(String id) {
    super(ErrorCode.ATTRIBUTE_DEFINITION_NOT_FOUND);
    addDetail("definitionId", id);
  }
}
