package project.closet.sse;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter.DataWithMediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseService {

    @Value("${sse.timeout}")
    private long timeout;

    private final SseEmitterRepository sseEmitterRepository;
    private final SseMessageRepository sseMessageRepository;

    public SseEmitter connect(UUID receiverId, UUID lastEventId) {
        SseEmitter sseEmitter = new SseEmitter(timeout);

        sseEmitter.onCompletion(() -> {
            log.debug("SSE onCompletion");
            sseEmitterRepository.delete(receiverId, sseEmitter);
        });
        sseEmitter.onTimeout(() -> {
            log.debug("SSE onTimeout");
            sseEmitterRepository.delete(receiverId, sseEmitter);
        });
        sseEmitter.onError((ex) -> {
            log.debug("SSE onError");
            sseEmitterRepository.delete(receiverId, sseEmitter);
        });

        sseEmitterRepository.save(receiverId, sseEmitter);
        try {
            sseEmitter.send(SseEmitter.event().name("ping"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Optional.ofNullable(lastEventId)
                .ifPresent(uuid -> {
                    sseMessageRepository.findAllByEventIdAfterAndReceiverId(uuid, receiverId)
                            .forEach(sseMessage -> {
                                try {
                                    sseEmitter.send(sseMessage.toEvent());
                                } catch (Exception e) {
                                    log.error("Error sending SSE message: {}", e.getMessage(), e);
                                }
                            });
                });

        return sseEmitter;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void cleanUp() {
        Set<DataWithMediaType> ping = SseEmitter.event()
                .name("ping")
                .build();
        sseEmitterRepository.findAll()
                .forEach(sseEmitter -> {
                    try {
                        sseEmitter.send(ping);
                    } catch (Exception e) {
                        log.error(e.getMessage(), e);
                        sseEmitter.completeWithError(e);
                    }
                });
    }

    public void send(UUID receiverId, String eventName, Object data) {
        sseEmitterRepository.findByReceiverId(receiverId)
                .ifPresent(sseEmitters -> {
                    SseMessage message = sseMessageRepository.save(
                            SseMessage.create(receiverId, eventName, data));
                    sseEmitters.forEach(sseEmitter -> {
                        try {
                            sseEmitter.send(message.toEvent());
                        } catch (Exception e) {
                            log.error("Error sending SSE message: {}", e.getMessage(), e);
                        }
                    });
                });
    }

    public void send(Collection<UUID> receiverIds, String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(SseMessage.create(receiverIds, eventName, data));
        Set<DataWithMediaType> event = message.toEvent();
        sseEmitterRepository.findAllByReceiverIdsIn(receiverIds)
                .forEach(sseEmitter -> {
                    try {
                        sseEmitter.send(event);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }

    public void broadcast(String eventName, Object data) {
        SseMessage message = sseMessageRepository.save(SseMessage.createBroadcast(eventName, data));
        Set<DataWithMediaType> event = message.toEvent();
        sseEmitterRepository.findAll()
                .forEach(sseEmitter -> {
                    try {
                        sseEmitter.send(event);
                    } catch (IOException e) {
                        log.error(e.getMessage(), e);
                    }
                });
    }
}
