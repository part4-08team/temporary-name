package project.closet.domain.clothes.service;

import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;

public interface AttributeService {

    ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest req);

}
