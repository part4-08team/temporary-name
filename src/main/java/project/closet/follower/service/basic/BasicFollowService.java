package project.closet.follower.service.basic;

import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.dto.response.FollowDto;
import project.closet.dto.response.FollowSummaryDto;
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
        int followerCount = followRepository.countByFolloweeId(userId);
        int followingCount = followRepository.countByFollowerId(userId);

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
}
