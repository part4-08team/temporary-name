package project.closet.follower.service.basic;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.dto.response.FollowDto;
import project.closet.dto.response.FollowListResponse;
import project.closet.dto.response.FollowSummaryDto;
import project.closet.exception.follow.FollowNotFoundException;
import project.closet.exception.user.UserNotFoundException;
import project.closet.follower.entity.Follow;
import project.closet.follower.repository.FollowRepository;
import project.closet.follower.service.FollowService;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicFollowService implements FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public FollowDto createFollow(FollowCreateRequest followCreateRequest) {
        log.debug("Creating follow for request: {}", followCreateRequest);
        UUID followerId = followCreateRequest.followerId();
        User follower = userRepository.findByIdWithProfile(followerId)
                .orElseThrow(() -> UserNotFoundException.withId(followerId));

        UUID followeeId = followCreateRequest.followeeId();
        User followee = userRepository.findByIdWithProfile(followeeId)
                .orElseThrow(() -> UserNotFoundException.withId(followeeId));

        Follow follow = Follow.builder()
                .follower(follower)
                .followee(followee)
                .build();
        return FollowDto.from(followRepository.save(follow));
    }

    @Transactional(readOnly = true)
    @Override
    public FollowSummaryDto getFollowSummary(UUID userId, UUID currentUserId) {
        log.debug("Getting follow summary for userId: {}, currentUserId: {}", userId,
                currentUserId);
        long followerCount = followRepository.countByFolloweeId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        Optional<Follow> myFollow = followRepository.findByFollowerIdAndFolloweeId(currentUserId,
                userId);
        boolean followedByMe = myFollow.isPresent();
        UUID followedByMeId = myFollow.map(Follow::getId).orElse(null);

        boolean followingMe = followRepository.existsByFollowerIdAndFolloweeId(userId,
                currentUserId);

        return new FollowSummaryDto(
                userId,
                followerCount,
                followingCount,
                followedByMe,
                followedByMeId,
                followingMe
        );
    }

    @Transactional(readOnly = true)
    @Override
    public FollowListResponse getFollowingList(UUID followerId, String cursor, UUID idAfter,
            int limit, String nameLike) {
        List<Follow> follows = followRepository.findFollowingsWithCursor(
                followerId,
                cursor != null ? Instant.parse(cursor) : null,
                idAfter,
                nameLike,
                limit
        );
        boolean hasNext = follows.size() > limit;
        List<Follow> pageItems = hasNext ? follows.subList(0, limit) : follows;
        List<FollowDto> followDtos = pageItems.stream()
                .map(FollowDto::from)
                .toList();

        String nextCursor = null;
        UUID nextIdAfter = null;
        if (hasNext) {
            Follow last = pageItems.get(pageItems.size() - 1);
            nextCursor = last.getCreatedAt().toString(); // ISO-8601 형식 문자열
            nextIdAfter = last.getId();
        }

        long totalCount = followRepository.countByFollowerId(followerId);
        return new FollowListResponse(
                followDtos,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                "DESCENDING"
        );
    }

    @Transactional(readOnly = true)
    @Override
    public FollowListResponse getFollowerList(UUID followeeId, String cursor, UUID idAfter,
            int limit, String nameLike) {
        Instant parsedCursor = (cursor != null) ? Instant.parse(cursor) : null;

        List<Follow> follows = followRepository.findFollowersWithCursor(
                followeeId, parsedCursor, idAfter, nameLike, limit + 1);

        boolean hasNext = follows.size() > limit;
        List<Follow> pageItems = hasNext ? follows.subList(0, limit) : follows;

        List<FollowDto> followDtos = pageItems.stream()
                .map(FollowDto::from)
                .toList();

        String nextCursor = null;
        UUID nextIdAfter = null;
        if (hasNext) {
            Follow last = pageItems.get(pageItems.size() - 1);
            nextCursor = last.getCreatedAt().toString();
            nextIdAfter = last.getId();
        }

        long totalCount = followRepository.countByFolloweeId(followeeId);

        return new FollowListResponse(
                followDtos,
                nextCursor,
                nextIdAfter,
                hasNext,
                totalCount,
                "createdAt",
                "DESC"
        );
    }

    @Transactional
    @Override
    public void cancelFollowById(UUID followId) {
        if (!followRepository.existsById(followId)) {
            throw FollowNotFoundException.withId(followId);
        }
        followRepository.deleteById(followId);
    }
}
