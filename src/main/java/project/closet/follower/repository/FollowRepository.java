package project.closet.follower.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.closet.follower.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowRepositoryCustom {

    long countByFolloweeId(UUID userId);

    long countByFollowerId(UUID userId);

    Optional<Follow> findByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);  // 이 사람이 나를 팔로우하고 있는지

    @Query("SELECT f.follower.id FROM Follow f WHERE f.followee.id = :followeeId")
    List<UUID> findFollowerIdsByFolloweeId(UUID followeeId);
}
// followee Id 를 기준으로 팔로워 ID 를 조회
