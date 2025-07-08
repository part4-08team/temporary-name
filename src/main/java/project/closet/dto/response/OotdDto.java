package project.closet.dto.response;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesType;

public record OotdDto(
        UUID clothesId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeWithDefDto> attributes
) {

    public static OotdDto from(Clothes clothes) {

        List<ClothesAttributeWithDefDto> attribute =
                ClothesAttributeWithDefDto.fromClothes(clothes);

        return new OotdDto(
                clothes.getId(),
                clothes.getName(),
                clothes.getImageUrl(),
                clothes.getType(),
                attribute
        );
    }
}
