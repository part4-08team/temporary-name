package project.closet.dm.service;

import java.time.Instant;
import java.util.UUID;
import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;
import project.closet.dto.response.DirectMessageDtoCursorResponse;

public interface DirectMessageService {

    DirectMessageDto sendMessage(DirectMessageCreateRequest directMessageCreateRequest);

    DirectMessageDtoCursorResponse getDirectMessages(UUID targetUserId, Instant cursor, UUID idAfter, int limit, UUID loginUserId);
}
