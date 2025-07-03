package project.closet.domain.clothes.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
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
}