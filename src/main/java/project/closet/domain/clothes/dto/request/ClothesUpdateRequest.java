package project.closet.domain.clothes.dto.request;

import jakarta.validation.Valid;

import java.util.List;

import project.closet.domain.clothes.dto.response.ClothesAttributeDto;
import project.closet.domain.clothes.entity.ClothesType;

public record ClothesUpdateRequest(
        String name,

        ClothesType type,

        @Valid
        List<ClothesAttributeDto> attributes
) {

}
