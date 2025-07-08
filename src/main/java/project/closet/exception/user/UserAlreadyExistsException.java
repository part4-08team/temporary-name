package project.closet.exception.user;

import project.closet.exception.ErrorCode;

public class UserAlreadyExistsException extends UserException {

    public UserAlreadyExistsException() {
        super(ErrorCode.DUPLICATE_USER);
    }

    public static UserAlreadyExistsException withEmail(String email) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("email", email);
        return exception;
    }

    public static UserAlreadyExistsException withName(String name) {
        UserAlreadyExistsException exception = new UserAlreadyExistsException();
        exception.addDetail("name", name);
        return exception;
    }
}
