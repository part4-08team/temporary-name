package project.closet.exception.follow;

import project.closet.exception.ErrorCode;

public class SelfFollowNotAllowedException extends FollowException {

    public SelfFollowNotAllowedException() {
        super(ErrorCode.SELF_FOLLOW_NOT_ALLOWED);
    }

    public static SelfFollowNotAllowedException withUserId(String userId) {
        SelfFollowNotAllowedException exception = new SelfFollowNotAllowedException();
        exception.addDetail("followerId", userId);
        exception.addDetail("followeeId", userId);
        return exception;
    }
}
