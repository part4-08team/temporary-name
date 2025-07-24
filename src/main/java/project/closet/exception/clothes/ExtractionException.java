package project.closet.exception.clothes;

import project.closet.exception.ErrorCode;

public class ExtractionException extends ClothesException {
    public ExtractionException(String url, Throwable cause) {
        super(ErrorCode.EXTRACTION_FAILED, cause);
        addDetail("url", url);
    }
}
