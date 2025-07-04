package project.closet.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;

import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.entity.AttributeSelectableValue;
import project.closet.domain.clothes.entity.Clothes;

public record ClothesDto(
        UUID id,
        UUID ownerId,
        String name,
        String imageUrl,
        String type,
        List<ClothesAttributeWithDefDto> attributes // 의상 속성
) {
    public static ClothesDto fromEntity(Clothes c) {
        List<ClothesAttributeWithDefDto> attrs = c.getAttributes().stream()
                .map(attr -> {
                    Attribute definition = attr.getDefinition();
                    List<String> selectableValues = definition.getSelectableValues().stream()
                            .map(AttributeSelectableValue::getValue)
                            .toList();

                    return new ClothesAttributeWithDefDto(
                            definition.getId(),
                            definition.getDefinitionName(),
                            selectableValues,
                            attr.getValue()
                    );
                })
                .toList();

        return new ClothesDto(
                c.getId(),
                c.getOwner().getId(),
                c.getName(),
                c.getImageUrl(),
                c.getType().name(),
                attrs
        );
    }
}
