package project.closet.user.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import org.springframework.util.StringUtils;
import project.closet.user.entity.Role;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepositoryCustom;

@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<User> findUsersWithCursor(String cursor, UUID idAfter, int limit, String sortBy,
            SortDirection direction, String emailLike, Role roleEqual, Boolean locked) {
        StringBuilder jpql = new StringBuilder("""
                SELECT u FROM User u
                WHERE 1=1
                """);

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(emailLike)) {
            jpql.append("AND u.email LIKE :emailLike ");
            params.put("emailLike", "%" + emailLike + "%");
        }
        if (roleEqual != null) {
            jpql.append("AND u.role = :roleEqual ");
            params.put("roleEqual", roleEqual);
        }
        if (locked != null) {
            jpql.append("AND u.locked = :locked ");
            params.put("locked", locked);
        }

        if (cursor != null && idAfter != null) {
            jpql.append("AND (u.").append(sortBy).append(" < :cursor OR (u.").append(sortBy)
                    .append(" = :cursor AND u.id > :idAfter)) ");
            params.put("cursor", cursor);
        }

        jpql.append("ORDER BY u.").append(sortBy).append(" ")
                .append(direction == SortDirection.ASCENDING ? "ASC" : "DESC")
                .append(", u.id ").append(direction == SortDirection.ASCENDING ? "ASC" : "DESC");

        TypedQuery<User> query = em.createQuery(jpql.toString(), User.class)
                .setMaxResults(limit + 1);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    @Override
    public long countAllUsers(String emailLike, Role roleEqual, Boolean locked) {
        StringBuilder jpql = new StringBuilder("""
                SELECT COUNT(u) FROM User u
                WHERE 1 = 1
                """);

        Map<String, Object> params = new HashMap<>();

        if (StringUtils.hasText(emailLike)) {
            jpql.append("AND u.email LIKE :emailLike ");
            params.put("emailLike", "%" + emailLike + "%");
        }
        if (roleEqual != null) {
            jpql.append("AND u.role = :roleEqual ");
            params.put("roleEqual", roleEqual);
        }
        if (locked != null) {
            jpql.append("AND u.locked = :locked ");
            params.put("locked", locked);
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        params.forEach(query::setParameter);

        return query.getSingleResult();
    }
}
