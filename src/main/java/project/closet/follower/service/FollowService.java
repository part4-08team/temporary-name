package project.closet.follower.service;

import java.util.UUID;
import project.closet.dto.request.FollowCreateRequest;
import project.closet.dto.response.FollowDto;
import project.closet.dto.response.FollowListResponse;
import project.closet.dto.response.FollowSummaryDto;

public interface FollowService {

    // 팔로우 생성
    FollowDto createFollow(FollowCreateRequest followCreateRequest);

    // 팔로우 요약 정보 조회
    FollowSummaryDto getFollowSummary(UUID userId, UUID currentUserId);

    // 팔로우 리스트 조회
    FollowListResponse getFollowingList(
            UUID followerId,
            String cursor,
            UUID idAfter,
            int limit,
            String nameLike
    );

    // 팔로워 리스트 조회
    FollowListResponse getFollowerList(
            UUID followeeId,
            String cursor,
            UUID idAfter,
            int limit,
            String nameLike
    );

    void cancelFollowById(UUID followId);
}
