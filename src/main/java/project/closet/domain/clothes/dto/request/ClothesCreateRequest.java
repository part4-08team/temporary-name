package project.closet.domain.clothes.dto.request;

import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import project.closet.domain.clothes.dto.response.ClothesAttributeDto;
import project.closet.domain.clothes.entity.ClothesType;

public record ClothesCreateRequest(
        @NotNull(message = "ownerId는 필수입니다.")
        UUID ownerId,
        @NotBlank(message = "name은 공백일 수 없습니다.")
        String name,
        @NotNull(message = "type은 필수입니다.")
        ClothesType type,
        List<ClothesAttributeDto> attributes
) {}
