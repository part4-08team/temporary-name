package project.closet.domain.clothes.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDto;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesAttribute;
import project.closet.domain.clothes.repository.AttributeRepository;
import project.closet.domain.clothes.repository.ClothesRepository;
import project.closet.exception.clothes.attribute.AttributeNotFoundException;
import project.closet.exception.user.UserNotFoundException;
import project.closet.user.repository.UserRepository;

@Service
@Transactional
@RequiredArgsConstructor
public class ClothesServiceImpl implements ClothesService {

    private final UserRepository userRepository;
    private final AttributeRepository attributeRepository;
    private final ClothesRepository clothesRepository;

    @Override
    public ClothesDto createClothes(
            ClothesCreateRequest request,
            MultipartFile image
    ) {
        var owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> UserNotFoundException.withId(request.ownerId()));

        // 이미지 처리 로직은 아직 미구현 (추후 추가)
        String imageUrl = "";

        Clothes clothes = new Clothes(
                owner,
                request.name(),
                imageUrl,
                request.type()
        );

        for (ClothesAttributeDto attrReq : request.attributes()) {
            var def = attributeRepository.findById(attrReq.definitionId())
                    .orElseThrow(() -> new AttributeNotFoundException(attrReq.definitionId().toString()));
            ClothesAttribute attr = new ClothesAttribute(def, attrReq.value());
            clothes.addAttribute(attr);
        }

        Clothes saved = clothesRepository.save(clothes);

        return ClothesDto.fromEntity(saved);
    }
}
