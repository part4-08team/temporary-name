package project.closet.domain.clothes.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.service.ClothesService;

@RestController
@RequestMapping("/api/clothes")
public class ClothesController {

    private final ClothesService clothesService;

    public ClothesController(ClothesService clothesService) {
        this.clothesService = clothesService;
    }

    @PostMapping
    public ResponseEntity<ClothesDto> createClothes(
            @RequestPart("request") @Valid ClothesCreateRequest request,
            @RequestPart(name = "image", required = false) MultipartFile image  // required=false
    ) {
        ClothesDto dto = clothesService.createClothes(request, image);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }
}
