package project.closet.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.hibernate.query.SortDirection;

public record NotificationDtoCursorResponse(
    List<NotificationDto> data,
    Instant nextCursor,
    UUID nextIdAfter,
    boolean hasNext,
    long totalCount,
    String sortBy,
    SortDirection sortDirection
) {

}
