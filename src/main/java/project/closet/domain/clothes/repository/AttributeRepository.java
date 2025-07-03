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
       WHERE (:keywordLike IS NULL OR a.definitionName LIKE %:keywordLike%)
         AND (:idAfter IS NULL OR a.id > :idAfter)
    """)
    Page<Attribute> searchAttributes(
            @Param("keywordLike") String keywordLike,
            @Param("idAfter") UUID idAfter,
            Pageable pageable
    );
}
