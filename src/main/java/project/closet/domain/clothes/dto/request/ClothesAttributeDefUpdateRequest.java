package project.closet.domain.clothes.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record ClothesAttributeDefUpdateRequest(
        @NotBlank
        String name,
        @NotEmpty
        List<@NotBlank  String> selectableValues
) {
}
