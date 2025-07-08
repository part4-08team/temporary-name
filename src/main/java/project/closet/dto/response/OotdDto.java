package project.closet.dto.response;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.dto.response.ClothesAttributeWithDefDto;
import project.closet.domain.clothes.entity.ClothesType;

public record OotdDto(
        UUID clothesId,
        String name,
        String imageUrl,
        ClothesType type,
        List<ClothesAttributeWithDefDto> attributes
) {

}
