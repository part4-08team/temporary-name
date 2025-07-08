package project.closet.dto.response;

import java.util.List;
import java.util.UUID;

public record FollowListResponse(
        List<FollowDto> data,
        String nextCursor,
        UUID nextIdAfter,
        boolean hasNext,
        long totalCount,
        String sortBy,
        String sortDirection
) {

}
