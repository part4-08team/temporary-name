package project.closet.domain.clothes.service;

import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesDto;

public interface ClothesService {

    ClothesDto createClothes(
            ClothesCreateRequest request,
            MultipartFile image
    );
}
