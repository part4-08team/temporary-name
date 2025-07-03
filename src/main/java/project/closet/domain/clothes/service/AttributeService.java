package project.closet.domain.clothes.service;

import java.util.UUID;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;

public interface AttributeService {

    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest req);

    ClothesAttributeDefDto update(UUID id, ClothesAttributeDefCreateRequest req);

    void delete(UUID id);
}
