package project.closet.exception.clothes;

import project.closet.exception.ErrorCode;

public class UnsupportedShopException extends ClothesException {
    public UnsupportedShopException(String url) {
        super(ErrorCode.UNSUPPORTED_SHOP);
        addDetail("url", url);
    }
}
