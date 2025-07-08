package project.closet.domain.clothes.dto.response;

import java.util.List;
import java.util.UUID;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.entity.AttributeSelectableValue;
import project.closet.domain.clothes.entity.Clothes;

public record ClothesAttributeWithDefDto(
        UUID definitionId,           // 속성 정의 ID
        String definitionName,       // 속성 정의 이름
        List<String> selectableValues, // 선택 가능한 값 목록
        String value                 // 속성 값
) {

    public static List<ClothesAttributeWithDefDto> fromClothes(Clothes clothes) {
        return clothes.getAttributes().stream()
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
    }
}
