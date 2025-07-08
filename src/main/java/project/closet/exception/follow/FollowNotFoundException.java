package project.closet.exception.follow;

import java.util.UUID;
import project.closet.exception.ErrorCode;

public class FollowNotFoundException extends FollowException {

    public FollowNotFoundException() {
        super(ErrorCode.FOLLOW_NOT_FOUND);
    }

    public static FollowNotFoundException withId(UUID followId) {
        FollowNotFoundException exception = new FollowNotFoundException();
        exception.addDetail("followId", followId);
        return exception;
    }
}
