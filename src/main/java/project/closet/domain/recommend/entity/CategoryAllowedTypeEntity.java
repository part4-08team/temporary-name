package project.closet.domain.recommend.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.clothes.entity.ClothesType;

@Entity
@Table(name = "category_allowed_type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(CategoryAllowedTypeId.class)
public class CategoryAllowedTypeEntity {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID categoryId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "clothes_type", nullable = false, updatable = false)
    private ClothesType clothesType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private TemperatureCategoryEntity category;

    @OneToMany(mappedBy = "allowedType", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CategoryAllowedDetailEntity> allowedDetails = new HashSet<>();

    public CategoryAllowedTypeEntity(UUID categoryId, ClothesType clothesType) {
        this.categoryId = categoryId;
        this.clothesType = clothesType;
    }

    /**
     * 상위 TemperatureCategoryEntity 와의 양방향 연관관계 설정
     */
    public void setCategory(TemperatureCategoryEntity category) {
        this.category = category;
        if (category != null) {
            this.categoryId = category.getId();
            category.getAllowedTypes().add(this);
        }
    }

    /** 세부 옵션 연관 설정 헬퍼 */
    public void addAllowedDetail(CategoryAllowedDetailEntity detail) {
        this.allowedDetails.add(detail);
        detail.setAllowedType(this);
    }
}
