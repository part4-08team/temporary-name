package project.closet.follower.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.follower.controller.api.FollowApi;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/follows")
public class FollowController implements FollowApi {

    @PostMapping
    @Override
    public ResponseEntity<Void> createFollow(
            @RequestBody @Valid FollowCreateRequest followCreateRequest
    ) {
        return null;
    }

    @GetMapping("/summary")
    @Override
    public ResponseEntity<Void> getFollowSummary(@RequestParam("userId") UUID userId) {
        return null;
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
