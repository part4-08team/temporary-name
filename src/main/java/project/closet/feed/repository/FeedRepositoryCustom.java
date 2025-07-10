package project.closet.feed.repository;

import java.util.List;
import java.util.UUID;
import org.hibernate.query.SortDirection;
import project.closet.feed.entity.Feed;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;

public interface FeedRepositoryCustom {

    List<Feed> findAllWithCursorAndFilters(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            SortDirection sortDirection,
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationType,
            UUID authorIdEqual
    );

    long countByFilters(
            String keywordLike,
            SkyStatus skyStatusEqual,
            PrecipitationType precipitationType,
            UUID authorIdEqual
    );
}
