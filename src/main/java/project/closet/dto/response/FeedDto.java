package project.closet.dto.response;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record FeedDto(
        UUID id,
        Instant createdAt,
        Instant updatedAt,
        UserSummary author,
        WeatherSummaryDto weather,
        List<OotdDto> ootds,
        String content,
        int likeCount,
        int commentCount,
        boolean likedByMe
) {

}
