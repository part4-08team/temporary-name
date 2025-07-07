package project.closet.domain.clothes.dto.response;

import java.util.UUID;

public record ClothesAttributeDto(
        UUID definitionId,   // 속성 정의 ID
        String value         // 속성 값
) {}
