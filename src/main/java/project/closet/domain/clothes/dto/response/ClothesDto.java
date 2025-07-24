package project.closet.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;

import project.closet.domain.clothes.entity.Clothes;

public record ClothesDto(
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        String type,
        List<ClothesAttributeWithDefDto> attributes // 의상 속성
) {
    public static ClothesDto fromEntity(Clothes c, String imageUrl) {
        List<ClothesAttributeWithDefDto> attrs =
                ClothesAttributeWithDefDto.fromClothes(c);

        return new ClothesDto(
                c.getId(),
                c.getOwner().getId(),
                c.getName(),
                imageUrl,
                c.getType().name(),
                attrs
        );
    }

    /** OG/스크래핑 전용 생성자 */
    public static ClothesDto extraction(String name, String imageUrl) {
        return new ClothesDto(null, null, name, imageUrl, null, null);
    }
}
