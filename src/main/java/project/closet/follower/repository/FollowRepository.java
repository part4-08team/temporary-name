package project.closet.follower.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.follower.entity.Follow;

public interface FollowRepository extends JpaRepository<Follow, UUID>, FollowRepositoryCustom  {

    long countByFolloweeId(UUID userId);
    long countByFollowerId(UUID userId);

    Optional<Follow> findByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);

    boolean existsByFollowerIdAndFolloweeId(UUID followerId, UUID followeeId);  // 이 사람이 나를 팔로우하고 있는지

}
