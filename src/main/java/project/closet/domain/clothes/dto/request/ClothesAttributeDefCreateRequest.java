package project.closet.domain.clothes.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ClothesAttributeDefCreateRequest(
        @NotBlank String name,
        @NotEmpty List<@NotBlank String> selectableValues
) {
}