package project.closet.dto.request;

import java.util.UUID;

public record DirectMessageCreateRequest(
        UUID receiverId,
        UUID senderId,
        String content
) {

}
