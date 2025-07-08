package project.closet.domain.clothes.service;

import java.util.UUID;

import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDtoCursorResponse;

public interface AttributeService {

    ClothesAttributeDefDto create(
            ClothesAttributeDefCreateRequest req
    );

    ClothesAttributeDefDto update(
            UUID id,
            ClothesAttributeDefUpdateRequest req
    );

    void delete(UUID id);

    ClothesAttributeDefDtoCursorResponse findAll(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            String sortDirection,
            String keywordLike
    );
}
