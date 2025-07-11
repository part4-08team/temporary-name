package project.closet.dm.service;

import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;

public interface DirectMessageService {

    DirectMessageDto sendMessage(DirectMessageCreateRequest directMessageCreateRequest);
}
