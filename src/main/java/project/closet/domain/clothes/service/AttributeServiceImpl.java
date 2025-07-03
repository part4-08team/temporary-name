package project.closet.domain.clothes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.dto.request.ClothesAttributeDefCreateRequest;
import project.closet.domain.clothes.dto.response.ClothesAttributeDefDto;
import project.closet.domain.clothes.entity.Attribute;
import project.closet.domain.clothes.repository.AttributeRepository;
import project.closet.exception.clothes.attribute.AttributeDuplicateException;

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
}
