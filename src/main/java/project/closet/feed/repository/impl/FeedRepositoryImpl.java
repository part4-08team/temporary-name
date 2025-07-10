package project.closet.feed.repository.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.SortDirection;
import project.closet.feed.entity.Feed;
import project.closet.feed.repository.FeedRepositoryCustom;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

@RequiredArgsConstructor
public class FeedRepositoryImpl implements FeedRepositoryCustom {

    private final EntityManager em;

    @Override
    public List<Feed> findAllWithCursorAndFilters(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationType,
            UUID authorIdEqual
    ) {
        StringBuilder jpql = new StringBuilder("""
                SELECT f FROM Feed f
                JOIN FETCH f.author a
                JOIN FETCH f.weather w
                WHERE 1 = 1
                """);

        Map<String, Object> params = new HashMap<>();

        // 필터 조건
        if (keywordLike != null && !keywordLike.isBlank()) {
            jpql.append("AND LOWER(f.content) LIKE LOWER(:keywordLike) ");
            params.put("keywordLike", "%" + keywordLike + "%");
        }
        if (skyStatusEqual != null) {
            jpql.append("AND w.skyStatus = :skyStatusEqual ");
            params.put("skyStatusEqual", skyStatusEqual);
        }
        if (precipitationType != null) {
            jpql.append("AND w.precipitationType = :precipitationType ");
            params.put("precipitationType", precipitationType);
        }
        if (authorIdEqual != null) {
            jpql.append("AND a.id = :authorIdEqual ");
            params.put("authorIdEqual", authorIdEqual);
        }
        if (cursor != null && !cursor.isBlank()) {
            switch (sortBy) {
                case "createdAt" -> {
                    Instant parsed = Instant.parse(cursor);
                    jpql.append(
                            sortDirection == SortDirection.DESCENDING ?
                                    "AND (f.createdAt < :cursor OR (f.createdAt = :cursor AND f.id < :idAfter)) " :
                                    "AND (f.createdAt > :cursor OR (f.createdAt = :cursor AND f.id > :idAfter)) "
                    );
                    params.put("cursor", parsed);
                    params.put("idAfter", idAfter);
                }
                case "likeCount" -> {
                    Integer parsed = Integer.parseInt(cursor);
                    jpql.append(
                            sortDirection == SortDirection.DESCENDING ?
                                    "AND (f.likeCount < :cursor OR (f.likeCount = :cursor AND f.id < :idAfter)) " :
                                    "AND (f.likeCount > :cursor OR (f.likeCount = :cursor AND f.id > :idAfter)) "
                    );
                    params.put("cursor", parsed);
                    params.put("idAfter", idAfter);
                }
                default -> throw new IllegalArgumentException("지원하지 않는 정렬 값입니다: " + sortBy);
            }
        }

        // 정렬 Order By
        jpql.append("ORDER BY f.")
                .append(sortBy).append(" ").append(toJpaDirection(sortDirection))
                .append(", f.id ").append(toJpaDirection(sortDirection));

        // 파라미터 매핑
        TypedQuery<Feed> query = em.createQuery(jpql.toString(), Feed.class);
        params.forEach(query::setParameter);
        query.setMaxResults(limit + 1);

        return query.getResultList();
    }

    @Override
    public long countByFilters(String keywordLike, SkyStatus skyStatusEqual,
            PrecipitationType precipitationType, UUID authorIdEqual) {
        StringBuilder jpql = new StringBuilder("""
                SELECT COUNT(f) FROM Feed f
                JOIN f.weather w
                JOIN f.author a
                WHERE 1 = 1
                """);

        Map<String, Object> params = new HashMap<>();

        if (keywordLike != null && !keywordLike.isBlank()) {
            jpql.append("AND LOWER(f.content) LIKE LOWER(:keywordLike) ");
            params.put("keywordLike", "%" + keywordLike + "%");
        }

        if (skyStatusEqual != null) {
            jpql.append("AND w.skyStatus = :skyStatusEqual ");
            params.put("skyStatusEqual", skyStatusEqual);
        }

        if (precipitationType != null) {
            jpql.append("AND w.precipitationType = :precipitationType ");
            params.put("precipitationType", precipitationType);
        }

        if (authorIdEqual != null) {
            jpql.append("AND a.id = :authorIdEqual ");
            params.put("authorIdEqual", authorIdEqual);
        }

        TypedQuery<Long> query = em.createQuery(jpql.toString(), Long.class);
        params.forEach(query::setParameter);
        return query.getSingleResult();
    }

    private String toJpaDirection(SortDirection direction) {
        return direction == SortDirection.DESCENDING ? "DESC" : "ASC";
    }
}
