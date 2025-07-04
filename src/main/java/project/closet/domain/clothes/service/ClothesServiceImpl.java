package project.closet.domain.clothes.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import project.closet.domain.clothes.dto.request.ClothesCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDto;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.clothes.dto.response.ClothesDtoCursorResponse;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesAttribute;
import project.closet.domain.clothes.entity.ClothesType;
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

    @Override
    public ClothesDtoCursorResponse findAll(
            String cursor,
            UUID idAfter,
            int limit,
            ClothesType typeEqual,
            UUID ownerId
    ) {
        // 1) 커서 디코딩 (다음 페이지 요청용)
        Instant lastCreatedAt = null;
        UUID    lastId        = null;
        if (cursor != null && !cursor.isBlank()) {
            String[] parts = cursor.split("::");
            lastCreatedAt = Instant.parse(parts[0]);
            lastId        = UUID.fromString(parts[1]);
        }

        // 2) Pageable 생성 (항상 createdAt DESC, id DESC 로 고정)
        var pageable = PageRequest.of(
                0,
                limit,
                Sort.by("createdAt").descending()
                        .and(Sort.by("id").descending())
        );

        // 3) 첫 페이지 vs 다음 페이지 분기
        Page<Clothes> page;
        if (lastCreatedAt == null) {
            // 첫 페이지
            page = clothesRepository.findByOwnerIdAndType(ownerId, typeEqual, pageable);
        } else {
            // 다음 페이지
            page = clothesRepository.findByOwnerAndTypeAfterCursor(
                    ownerId, typeEqual, lastCreatedAt, lastId, pageable
            );
        }

        List<Clothes> content = page.getContent();

        // 4) 다음 커서 계산
        String nextCursor   = null;
        UUID   nextIdAfterR = null;
        if (page.hasNext() && !content.isEmpty()) {
            Clothes last = content.get(content.size() - 1);
            nextCursor   = last.getCreatedAt().toString()
                           + "::" + last.getId();
            nextIdAfterR = last.getId();
        }

        List<ClothesDto> data = content.stream()
                .map(ClothesDto::fromEntity)
                .collect(Collectors.toList());

        return new ClothesDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfterR,
                page.hasNext(),
                page.getTotalElements(),
                "createdAt",
                "DESCENDING"
        );
    }
}
