package project.closet.domain.recommend.dto.responses;

import java.util.List;
import java.util.UUID;

public record RecommendationDto(
        UUID weatherId,
        UUID userId,
        List<ClothesForRecommendDto> clothes
) {

}
