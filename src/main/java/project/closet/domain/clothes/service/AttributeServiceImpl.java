package project.closet.domain.clothes.service;

import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDtoCursorResponse;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.repository.AttributeRepository;
import project.closet.exception.clothes.attribute.AttributeDuplicateException;
import project.closet.exception.clothes.attribute.AttributeNotFoundException;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository repo;

    @Override
    @Transactional
    public ClothesAttributeDefDto create(ClothesAttributeDefCreateRequest req) {
        if (repo.existsByDefinitionName(req.name())) {
            throw new AttributeDuplicateException();
        }
        Attribute entity = new Attribute(req.name(), req.selectableValues());
        repo.save(entity);

        return ClothesAttributeDefDto.of(entity);
    }

    @Override
    @Transactional
    public ClothesAttributeDefDto update(UUID id, ClothesAttributeDefCreateRequest req) {
        Attribute e = repo.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException(id.toString()));

        if (!e.getDefinitionName().equals(req.name()) &&
                repo.existsByDefinitionName(req.name())) {
            throw new AttributeDuplicateException();
        }

        e.setDefinitionName(req.name());
        e.setSelectableValues(req.selectableValues());

        return ClothesAttributeDefDto.of(e);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attribute e = repo.findById(id)
                .orElseThrow(() -> new AttributeNotFoundException(id.toString()));
        repo.delete(e);
    }

    @Override
    @Transactional(readOnly = true)
    public ClothesAttributeDefDtoCursorResponse findAll(
            String cursor,
            UUID idAfter,
            int limit,
            String sortBy,
            String sortDirection,
            String keywordLike
    ) {
        // 1) cursor(String)를 page 번호로 파싱
        int page = 0;
        if (cursor != null && !cursor.isBlank()) {
            try { page = Integer.parseInt(cursor); }
            catch (NumberFormatException ignored) {}
        }

        // 2) Pageable 생성
        var pageable = PageRequest.of(
                page,
                limit,
                Sort.by(Sort.Direction.fromString(sortDirection), sortBy)
        );

        // 3) Repository 호출
        Page<Attribute> pageResult =
                repo.searchAttributes(keywordLike, idAfter, pageable);

        // 4) DTO 변환
        var data = pageResult.stream()
                .map(ClothesAttributeDefDto::of)
                .collect(Collectors.toList());

        // 5) 다음 커서 계산
        String nextCursor = pageResult.hasNext()
                ? String.valueOf(pageResult.getNumber() + 1)
                : null;
        UUID nextIdAfter = pageResult.hasNext() && !data.isEmpty()
                ? data.get(data.size() - 1).id()
                : null;

        // 6) 응답 조립
        return new ClothesAttributeDefDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                pageResult.hasNext(),
                pageResult.getTotalElements(),
                sortBy,
                sortDirection
        );
    }

}
