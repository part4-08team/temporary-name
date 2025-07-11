package project.closet.domain.clothes.service;

import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.dto.response.ClothesDtoCursorResponse;
import project.closet.domain.clothes.entity.ClothesType;

public interface ClothesService {

    ClothesDto createClothes(
            ClothesCreateRequest request,
            MultipartFile image
    );

    ClothesDtoCursorResponse findAll(
            String cursor,
            UUID idAfter,
            int limit,
            ClothesType typeEqual,
            UUID ownerId
    );

    void deleteClothesById(
            UUID clothesId
    );
}
