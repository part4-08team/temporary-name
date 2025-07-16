package project.closet.domain.recommend.dto.responses;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.entity.ClothesType;

public record RecommendationDto(
        UUID weatherId,
        UUID userId,
        List<ClothesDto> clothes   // 내부 DTO 대신 ClothesDto
) {}