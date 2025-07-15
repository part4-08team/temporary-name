package project.closet.event;

import java.util.UUID;

public record DirectMessageSentEvent(
        UUID receiverId,
        String senderUsername,
        String messageContent
) {

}
