package project.closet.exception.clothes;

import java.util.UUID;
import project.closet.exception.ErrorCode;

public class ClothesNotFoundException extends ClothesException {

    protected ClothesNotFoundException() {
        super(ErrorCode.CLOTHES_NOT_FOUND);
    }

    public static ClothesNotFoundException withId(UUID clothesId) {
        ClothesNotFoundException exception = new ClothesNotFoundException();
        exception.addDetail("clothesId", clothesId);
        return exception;
    }
}
