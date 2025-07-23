package project.closet.domain.recommend.dto.responses;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesType;

public record ClothesForRecommendDto(
        UUID clothesId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeWithDefDto> attributes
) {

    public ClothesForRecommendDto(Clothes clothes, String imageUrl) {
        this(
                clothes.getId(),
                clothes.getName(),
                imageUrl,
                clothes.getType(),
                ClothesAttributeWithDefDto.fromClothes(clothes)
        );
    }
}
