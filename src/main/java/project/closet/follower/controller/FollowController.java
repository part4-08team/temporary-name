package project.closet.follower.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.dto.response.FollowDto;
import project.closet.dto.response.FollowSummaryDto;
import project.closet.follower.controller.api.FollowApi;
import project.closet.follower.service.FollowService;
import project.closet.security.ClosetUserDetails;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follows")
public class FollowController implements FollowApi {

    private final FollowService followService;

    @PostMapping
    @Override
    public ResponseEntity<FollowDto> createFollow(
            @RequestBody @Valid FollowCreateRequest followCreateRequest
    ) {
        FollowDto follow = followService.createFollow(followCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(follow);
    }

    @GetMapping("/summary")
    @Override
    public ResponseEntity<FollowSummaryDto> getFollowSummary(
            @RequestParam("userId") UUID userId,
            @AuthenticationPrincipal ClosetUserDetails userDetails
    ) {
        log.debug("{}", userDetails.getUserId());
        FollowSummaryDto followSummary =
                followService.getFollowSummary(userId, userDetails.getUserId());
        return ResponseEntity.ok(followSummary);
    }

    @GetMapping("/followings")
    @Override
    public ResponseEntity<Void> getFollowingList(
            @RequestParam(name = "followerId") UUID followerId,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "nameLike", required = false) String nameLike
    ) {
        return null;
    }

    @GetMapping("/followers")
    @Override
    public ResponseEntity<Void> getFolloweeList(
            @RequestParam(name = "followeeId") UUID followeeId,
            @RequestParam(name = "cursor", required = false) String cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit", defaultValue = "20") int limit,
            @RequestParam(name = "nameLike") String nameLike
    ) {
        return null;
    }

    @DeleteMapping("{followId}")
    @Override
    public ResponseEntity<Void> cancelFollow(@PathVariable("followId") UUID followId) {
        return null;
    }
}
