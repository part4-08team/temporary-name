package project.closet.dto.response;

import java.util.UUID;

public record FollowSummaryDto(
        UUID followeeId,    // 요약 대상 유저 ID
        int followerCount,  // 이 유저를 팔로우하는 사람 수
        int followingCount, // 이 유저가 팔로우하는 사람 수
        boolean followedByMe,   // 현재 로그인한 유저가 이 유저를 팔로우 중인지 여부
        UUID followedByMeId,    // 내가 이 유저를 팔로우하고 있다면 Follow 테이블의 ID
        boolean followingMe // 이 유저가 나를 팔로우 중인지 여부
) {

}
