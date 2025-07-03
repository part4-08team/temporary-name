package project.closet.domain.clothes.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.domain.clothes.entity.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

    boolean existsByDefinitionName(String definitionName);

}
