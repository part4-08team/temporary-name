package project.closet.domain.users.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.closet.domain.users.User;

public interface UserRepository extends JpaRepository<User, UUID> {

  // user 조회 시 profile도 한번에 가져오는거 안 막으려면 이거 제거
  @Query("SELECT u FROM User u WHERE u.id = :userId")
  Optional<User> findById(@Param("userId") UUID userId);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);
}
