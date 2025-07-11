package project.closet.dm.controller;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dm.controller.api.DirectMessageApi;
import project.closet.dm.service.DirectMessageService;
import project.closet.dto.response.DirectMessageDtoCursorResponse;
import project.closet.security.ClosetUserDetails;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/direct-messages")
public class DirectMessageController implements DirectMessageApi {

    private final DirectMessageService directMessageService;

    @GetMapping
    @Override
    public ResponseEntity<DirectMessageDtoCursorResponse> getDirectMessage(
            @RequestParam(name = "userId") UUID userId,
            @RequestParam(name = "cursor", required = false) Instant cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit", defaultValue = "15") int limit,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        UUID loginUserId = closetUserDetails.getUserId();
        DirectMessageDtoCursorResponse directMessages =
                directMessageService.getDirectMessages(userId, cursor, idAfter, limit, loginUserId);
        return ResponseEntity.ok(directMessages);
    }
}
