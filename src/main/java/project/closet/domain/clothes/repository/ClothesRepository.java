package project.closet.domain.clothes.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesType;

public interface ClothesRepository extends JpaRepository<Clothes, UUID> {


    List<Clothes> findByOwnerId(UUID ownerId);

    List<Clothes> findByOwnerIdAndType(UUID ownerId, ClothesType typeEqual);
    /**
     * 첫 페이지 조회: cursor 없음.
     */
    Page<Clothes> findByOwnerIdAndType(
            UUID ownerId,
            ClothesType typeEqual,
            Pageable pageable
    );

    /**
     * 두 번째 이후 페이지: cursor 기반 복합 조회. lastCreatedAt, lastId 는 non-null 로만 호출됩니다.
     */
    @Query("""
            SELECT c
              FROM Clothes c
             WHERE c.owner.id = :ownerId
               AND ( :typeEqual IS NULL OR c.type = :typeEqual )
               AND (
                    c.createdAt <  :lastCreatedAt
                 OR (c.createdAt =  :lastCreatedAt AND c.id < :lastId)
               )
            """)
    Page<Clothes> findByOwnerAndTypeAfterCursor(
            @Param("ownerId") UUID ownerId,
            @Param("typeEqual") ClothesType typeEqual,
            @Param("lastCreatedAt") Instant lastCreatedAt,
            @Param("lastId") UUID lastId,
            Pageable pageable
    );

    @Query("""
                SELECT DISTINCT c FROM Clothes c
                LEFT JOIN FETCH c.attributes a
                LEFT JOIN FETCH a.definition
                WHERE c.id IN :ids
            """)
    List<Clothes> findAllByIdInWithAttributes(@Param("ids") List<UUID> ids);
}
