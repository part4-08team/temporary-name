package project.closet.domain.clothes.controller;

import jakarta.validation.Valid;

import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.request.ClothesUpdateRequest;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.dto.response.ClothesDtoCursorResponse;
import project.closet.domain.clothes.entity.ClothesType;
import project.closet.domain.clothes.service.ClothesExtractionService;
import project.closet.domain.clothes.service.ClothesService;

@RestController
@RequestMapping("/api/clothes")
public class ClothesController {

    private final ClothesService clothesService;
    private final ClothesExtractionService extractionService;

    public ClothesController(ClothesService clothesService, ClothesExtractionService extractionService) {
        this.clothesService = clothesService;
        this.extractionService = extractionService;
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

    @GetMapping
    public ResponseEntity<ClothesDtoCursorResponse> findAll(
            @RequestParam(name = "cursor", required = false, defaultValue = "") String cursor,
            @RequestParam(name = "idAfter", required = false) UUID idAfter,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "typeEqual", required = false) ClothesType typeEqual,
            @RequestParam(name = "ownerId") UUID ownerId
    ) {
        ClothesDtoCursorResponse resp = clothesService.findAll(
                cursor.isBlank() ? null : cursor,  // 빈 문자열도 null로 처리
                idAfter,
                limit,
                typeEqual,
                ownerId
        );
        return ResponseEntity.ok(resp);
    }

    @DeleteMapping("/{clothesId}")
    public ResponseEntity<Void> deleteClothes(
            @PathVariable("clothesId") UUID clothesId
    ) {
        clothesService.deleteClothesById(clothesId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping(
            value = "/{clothesId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ClothesDto> updateClothes(
            @PathVariable("clothesId") UUID clothesId,
            @RequestPart("request") @Valid ClothesUpdateRequest request,
            @RequestPart(name = "image", required = false) MultipartFile image
    ) {
        ClothesDto updated = clothesService.updateClothes(clothesId, request, image);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/extractions")
    public ResponseEntity<ClothesDto> extract(@RequestParam("url") String url) {
        ClothesDto dto = extractionService.extractFromUrl(url);
        return ResponseEntity.ok(dto);
    }

}

