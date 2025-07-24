package project.closet.domain.recommend.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import project.closet.domain.recommend.entity.TemperatureCategoryEntity;

@DataJpaTest(properties = {
        // H2 인메모리 DB에 엔티티 어노테이션 기반으로 테이블을 CREATE/DROP
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class TemperatureCategoryRepositoryTest {

    @Autowired
    private TemperatureCategoryRepository repo;

    private TemperatureCategoryEntity hot;
    private TemperatureCategoryEntity warm;

    @BeforeEach
    void setUp() {
        // VERY_HOT: 28 <= temp < +∞
        hot = new TemperatureCategoryEntity("HOT", 23.0, 28.0);
        repo.save(hot);
        // WARM: 20 <= temp < 23
        warm = new TemperatureCategoryEntity("WARM", 20.0, 23.0);
        repo.save(warm);
    }

    @Test
    void whenTemp21_thenFindWarm() {
        TemperatureCategoryEntity found = repo
                .findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(21.0, 21.0)
                .orElseThrow();
        assertThat(found.getName()).isEqualTo("WARM");
    }

    @Test
    void whenTemp25_thenFindHot() {
        TemperatureCategoryEntity found = repo
                .findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(25.0, 25.0)
                .orElseThrow();
        assertThat(found.getName()).isEqualTo("HOT");
    }

    @Test
    void whenTempBelowAny_thenEmpty() {
        // e.g. temp less than 20 (only warm/hot present)
        assertThat(repo.findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(10.0, 10.0))
                .isEmpty();
    }
}