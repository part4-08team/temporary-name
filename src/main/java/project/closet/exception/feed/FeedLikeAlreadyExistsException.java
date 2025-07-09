package project.closet.exception.feed;

import java.util.UUID;
import project.closet.exception.ErrorCode;

public class FeedLikeAlreadyExistsException extends FeedException {

    public FeedLikeAlreadyExistsException() {
        super(ErrorCode.FEED_ALREADY_EXISTS);
    }

    public static FeedLikeAlreadyExistsException of(UUID userId, UUID feedId) {
        FeedLikeAlreadyExistsException exception = new FeedLikeAlreadyExistsException();
        exception.addDetail("userId", userId.toString());
        exception.addDetail("feedId", feedId.toString());
        return exception;
    }
}
