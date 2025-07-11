package project.closet.dm.service.basic;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dm.service.MessageService;
import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;

@Service
@RequiredArgsConstructor
public class BasicMessageService implements MessageService {

    @Transactional
    @Override
    public DirectMessageDto sendMessage(DirectMessageCreateRequest directMessageCreateRequest) {
        return null;
    }
}
