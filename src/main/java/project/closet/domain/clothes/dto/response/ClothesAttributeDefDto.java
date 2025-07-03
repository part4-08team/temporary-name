package project.closet.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.entity.AttributeSelectableValue;

public record ClothesAttributeDefDto(
        UUID id,
        String name,
        List<String> selectableValues
) {
    public static ClothesAttributeDefDto of(Attribute e) {
        List<String> values = e.getSelectableValues().stream()
                .map(AttributeSelectableValue::getValue)
                .toList();  // Java 16+ 혹은 Collectors.toList()

        return new ClothesAttributeDefDto(
                e.getId(),
                e.getDefinitionName(),
                values
        );
    }
}
