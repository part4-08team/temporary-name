package project.closet.dto.request;

import java.util.UUID;

public record CommentCreateRequest(
        UUID feedId,
        UUID authorId,
        String content
) {

}
