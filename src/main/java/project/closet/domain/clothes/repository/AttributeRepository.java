package project.closet.domain.clothes.repository;

import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.closet.domain.clothes.entity.Attribute;

public interface AttributeRepository extends JpaRepository<Attribute, UUID> {

    boolean existsByDefinitionName(String definitionName);

    @Query("""
    SELECT a
      FROM Attribute a
     WHERE (:keywordLike IS NULL OR a.definitionName ILIKE %:keywordLike%)
       AND (
            (:lastName IS NULL AND :lastId IS NULL) OR
            (a.definitionName > :lastName) OR
            (a.definitionName = :lastName AND a.id > :lastId)
       )
     ORDER BY a.definitionName ASC, a.id ASC
""")
    Page<Attribute> searchAttributesByCompositeCursor(
            @Param("keywordLike") String keywordLike,
            @Param("lastName") String lastName,
            @Param("lastId") UUID lastId,
            Pageable pageable
    );
}
