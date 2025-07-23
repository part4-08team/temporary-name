package project.closet.domain.recommend.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "temperature_category")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TemperatureCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "min_temp", nullable = false)
    private double minTemp;

    @Column(name = "max_temp", nullable = false)
    private double maxTemp;

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CategoryAllowedTypeEntity> allowedTypes = new HashSet<>();

    // 생성자
    public TemperatureCategoryEntity(String name, double minTemp, double maxTemp) {
        this.name = name;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
    }

    /**
     * CategoryAllowedTypeEntity 를 양방향 연관관계로 연결
     */
    public void addAllowedType(CategoryAllowedTypeEntity type) {
        this.allowedTypes.add(type);
        type.setCategory(this);   // CategoryAllowedTypeEntity#setCategory(...) 호출
    }

}
