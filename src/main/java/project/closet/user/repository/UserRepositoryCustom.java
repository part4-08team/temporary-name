package project.closet.user.repository;

import java.util.List;
import java.util.UUID;
import org.hibernate.query.SortDirection;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;

public interface UserRepositoryCustom {
    List<User> findUsersWithCursor(String cursor, UUID idAfter, int limit, String sortBy,
            SortDirection direction, String emailLike, Role roleEqual, Boolean locked);

    long countAllUsers(String emailLike, Role roleEqual, Boolean locked);
}
