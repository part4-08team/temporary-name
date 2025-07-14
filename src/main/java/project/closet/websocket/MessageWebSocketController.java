package project.closet.websocket;

import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import project.closet.dm.service.DirectMessageService;
import project.closet.dto.request.DirectMessageCreateRequest;
import project.closet.dto.response.DirectMessageDto;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MessageWebSocketController {

    private final DirectMessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/direct-messages_send")
    public void sendMessage(
            @Payload DirectMessageCreateRequest directMessageCreateRequest
    ) {
        log.info("텍스트 메시지 생성 요청: request={}", directMessageCreateRequest);
        DirectMessageDto createdMessage = messageService.sendMessage(directMessageCreateRequest);
        log.debug("텍스트 메시지 생성 응답: {}", createdMessage);
        String dmKey = getDmKey(directMessageCreateRequest);
        String destination = "/sub/direct-messages_" + dmKey;
        messagingTemplate.convertAndSend(destination, createdMessage);
    }

    private static String getDmKey(DirectMessageCreateRequest directMessageCreateRequest) {
        return Stream.of(
                        directMessageCreateRequest.senderId().toString(),
                        directMessageCreateRequest.receiverId().toString()
                ).sorted()
                .collect(Collectors.joining("_"));
    }

}
