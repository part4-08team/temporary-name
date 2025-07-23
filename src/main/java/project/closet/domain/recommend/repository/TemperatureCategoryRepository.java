package project.closet.domain.recommend.repository;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.domain.recommend.entity.TemperatureCategoryEntity;

public interface TemperatureCategoryRepository
        extends JpaRepository<TemperatureCategoryEntity, UUID> {

    /**
     * adjustedTemp가 이 구간(minTemp ≤ temp < maxTemp)에 속하는 첫 카테고리
     */
    Optional<TemperatureCategoryEntity>
    findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(
            double minTemp,
            double maxTemp
    );
}