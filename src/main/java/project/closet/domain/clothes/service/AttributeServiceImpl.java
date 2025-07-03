package project.closet.domain.clothes.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
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
}
