package project.closet.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.entity.Attribute;

public record ClothesAttributeDefDto(
        UUID id,
        String name,
        List<String> selectableValues
) {
    public static ClothesAttributeDefDto of(Attribute e) {
        return new ClothesAttributeDefDto(
                e.getId(),              // BaseEntity 에서 상속된 id
                e.getDefinitionName(),
                e.getSelectableValues()
        );
    }
}
