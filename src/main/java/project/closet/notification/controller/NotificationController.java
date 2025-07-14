package project.closet.notification.controller;

import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.response.NotificationDtoCursorResponse;
import project.closet.notification.controller.api.NotificationApi;
import project.closet.notification.service.NotificationService;
import project.closet.security.ClosetUserDetails;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController implements NotificationApi {

    private final NotificationService notificationService;

    @GetMapping
    @Override
    public ResponseEntity<NotificationDtoCursorResponse> findAll(
            @RequestParam(name = "cursor", required = false) Instant cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit") int limit,
            @AuthenticationPrincipal ClosetUserDetails closetUserDetails
    ) {
        NotificationDtoCursorResponse response =
                notificationService.getNotifications(closetUserDetails.getUserId(), cursor, idAfter, limit);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{notificationId}")
    @Override
    public ResponseEntity<Void> delete(@PathVariable("notificationId") UUID notificationId) {
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.noContent().build();
    }
}
