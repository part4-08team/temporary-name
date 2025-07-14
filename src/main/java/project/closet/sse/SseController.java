package project.closet.sse;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import project.closet.security.ClosetUserDetails;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/sse")
public class SseController {

    private final SseService sseService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @AuthenticationPrincipal ClosetUserDetails userDetails,
            @RequestParam(value = "LastEventId", required = false) UUID lastEventId
    ) {
        UUID userId = userDetails.getUserId();

        return sseService.connect(userId, lastEventId);
    }
}
