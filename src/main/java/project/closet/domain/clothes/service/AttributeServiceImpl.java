package project.closet.domain.clothes.service;

import java.util.UUID;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefUpdateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDtoCursorResponse;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.repository.AttributeRepository;
import project.closet.event.ClothesAttributeCreatEvent;
import project.closet.exception.clothes.attribute.AttributeDuplicateException;
import project.closet.exception.clothes.attribute.AttributeNotFoundException;

@Service
@RequiredArgsConstructor
public class AttributeServiceImpl implements AttributeService {

    private final AttributeRepository repo;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public ClothesAttributeDefDto create(
            ClothesAttributeDefCreateRequest req
    ) {
        if (repo.existsByDefinitionName(req.name())) {
            throw new AttributeDuplicateException();
        }
        Attribute entity = new Attribute(
                req.name(),
                req.selectableValues()
        );
        repo.save(entity);
        // 의상 속성 추가 시 알림 생성 이벤트 발생.
        eventPublisher.publishEvent(new ClothesAttributeCreatEvent(entity.getDefinitionName()));

        return ClothesAttributeDefDto.of(entity);
    }

    @Override
    @Transactional
    public ClothesAttributeDefDto update(
            UUID id,
            ClothesAttributeDefUpdateRequest req
    ) {
        Attribute e = repo.findById(id)
                .orElseThrow(
                        () -> new AttributeNotFoundException(
                                id.toString()
                        )
                );

        if (!e.getDefinitionName().equals(req.name())
                && repo.existsByDefinitionName(req.name())) {
            throw new AttributeDuplicateException();
        }

        e.setDefinitionName(req.name());
        e.setSelectableValues(req.selectableValues());

        // 의상 속성 수정 시 알림 생성 이벤트 발생.
        return ClothesAttributeDefDto.of(e);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        Attribute e = repo.findById(id)
                .orElseThrow(
                        () -> new AttributeNotFoundException(
                                id.toString()
                        )
                );
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
        String lastName = null;
        UUID lastId = null;
        if (cursor != null && !cursor.isBlank()) {
            try {
                String[] parts = cursor.split("::");
                if (parts.length == 2) {
                    lastName = parts[0];
                    lastId = UUID.fromString(parts[1]);
                }
            } catch (Exception ignored) {
            }
        }

        Pageable pageable = PageRequest.of(
                0,
                limit,
                Sort.by("definitionName").ascending()
                        .and(Sort.by("id"))
        );

        var pageResult = repo.searchAttributesByCompositeCursor(
                keywordLike,
                lastName,
                lastId,
                pageable
        );

        var data = pageResult.stream()
                .map(ClothesAttributeDefDto::of)
                .collect(Collectors.toList());

        String nextCursor = pageResult.hasNext() && !data.isEmpty()
                ? data.get(data.size() - 1)
                .name() + "::" + data.get(data.size() - 1).id()
                : null;

        UUID nextIdAfter = pageResult.hasNext() && !data.isEmpty()
                ? data.get(data.size() - 1).id()
                : null;

        return new ClothesAttributeDefDtoCursorResponse(
                data,
                nextCursor,
                nextIdAfter,
                pageResult.hasNext(),
                pageResult.getTotalElements(),
                "definitionName",
                "ASCENDING"
        );
    }
}
