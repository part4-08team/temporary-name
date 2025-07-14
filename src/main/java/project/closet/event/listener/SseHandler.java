package project.closet.event.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.closet.sse.SseService;

@Slf4j
@Component
@RequiredArgsConstructor
public class SseHandler {

    private final SseService sseService;

}
