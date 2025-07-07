package project.closet.domain.clothes.controller;

import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDtoCursorResponse;
import project.closet.domain.clothes.service.AttributeService;

@RestController
@RequestMapping("/api/clothes/attribute-defs")
@Validated
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ClothesAttributeDefDto create(
            @Valid @RequestBody ClothesAttributeDefCreateRequest req
    ) {
        return attributeService.create(req);
    }

    @PatchMapping("/{definitionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClothesAttributeDefDto update(
            @PathVariable UUID definitionId,
            @Valid @RequestBody ClothesAttributeDefUpdateRequest req
    ) {
        return attributeService.update(definitionId, req);
    }

    @DeleteMapping("/{definitionId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID definitionId) {
        attributeService.delete(definitionId);
    }

    @GetMapping
    public ClothesAttributeDefDtoCursorResponse findAll(
            @RequestParam(required = false) String cursor,
            @RequestParam(required = false) UUID idAfter,
            @RequestParam(name = "limit") int limit,
            @RequestParam(name = "sortBy",        defaultValue = "definitionName") String sortBy,
            @RequestParam(name = "sortDirection", defaultValue = "ASCENDING") String sortDirection,
            @RequestParam(required = false) String keywordLike
    ) {
        return attributeService.findAll(
                cursor, idAfter, limit, sortBy, sortDirection, keywordLike
        );
    }

}