package project.closet.follower.service.basic;

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
        log.debug("Getting follow summary for userId: {}, currentUserId: {}", userId, currentUserId);
        int followerCount = followRepository.countByFolloweeId(userId);
        int followingCount = followRepository.countByFollowerId(userId);

        Optional<Follow> myFollow = followRepository.findByFollowerIdAndFolloweeId(currentUserId, userId);
        boolean followedByMe = myFollow.isPresent();
        UUID followedByMeId = myFollow.map(Follow::getId).orElse(null);

        boolean followingMe = followRepository.existsByFollowerIdAndFolloweeId(userId, currentUserId);

        return new FollowSummaryDto(
                userId,
                followerCount,
                followingCount,
                followedByMe,
                followedByMeId,
                followingMe
        );
    }

    @Override
    public FollowListResponse getFollowingList(UUID followerId, String cursor, UUID idAfter,
            int limit, String nameLike) {
        // 1. 커서 페이징 기준에 맞게 정렬 조건 및 필터 조건을 설정한다.
        //    - createdAt 또는 idAfter 를 기준으로 정렬
        //    - nameLike 가 있는 경우 followee name 필터 추가

        // 2. followerId 를 기준으로 Follow 테이블에서 followee 정보를 가져오는 쿼리를 작성한다.
        //    - limit + 1 만큼 조회하여 다음 페이지 존재 여부 판단

        // 3. 결과 Follow 목록을 FollowDto 리스트로 매핑한다.
        //    - followee, follower 정보를 UserSummary로 구성
        //    - 최대 limit 개수로 자른다 (limit + 1로 조회했기 때문에)

        // 4. hasNext 값을 계산한다.
        //    - 조회한 결과가 limit보다 많다면 hasNext = true

        // 5. nextCursor 및 nextIdAfter 값을 계산한다.
        //    - 마지막 Follow 객체의 createdAt 또는 id 기준으로 커서 생성

        // 6. 전체 followerId 기준 totalCount 를 계산한다.
        //    - 팔로우 수 세기 (추후 캐시 적용 고려 가능)

        // 7. FollowListResponse 객체를 생성하여 반환한다.
        //    - data, nextCursor, nextIdAfter, hasNext, totalCount, sortBy, sortDirection 포함
        return null;
    }
}
