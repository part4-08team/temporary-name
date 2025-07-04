package project.closet.domain.clothes.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record ClothesAttributeDefUpdateRequest(
        @NotBlank String name,
        @NotEmpty List<@NotBlank String> selectableValues
) {
}