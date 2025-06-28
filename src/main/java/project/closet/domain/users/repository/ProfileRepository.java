package project.closet.domain.users.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.closet.domain.users.Profile;

public interface ProfileRepository extends JpaRepository<Profile, UUID> {

  @Query("SELECT p FROM Profile p WHERE p.user.id = :userId")
  Optional<Profile> findByUserId(@Param("userId") UUID userId);
}
