package project.closet.domain.recommend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.clothes.entity.ClothesType;

@Entity
@Table(name = "category_allowed_detail")
@Getter
@NoArgsConstructor
@IdClass(CategoryAllowedDetailId.class)
public class CategoryAllowedDetailEntity {

    @Id
    @Column(name = "category_id", nullable = false, updatable = false)
    private UUID categoryId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "clothes_type", nullable = false, updatable = false)
    private ClothesType clothesType;

    @Id
    @Column(name = "detail_value", nullable = false, updatable = false)
    private String detailValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "category_id", referencedColumnName = "category_id", insertable = false, updatable = false),
            @JoinColumn(name = "clothes_type", referencedColumnName = "clothes_type", insertable = false, updatable = false)
    })
    private CategoryAllowedTypeEntity allowedType;

    // 생성자
    public CategoryAllowedDetailEntity(UUID categoryId, ClothesType clothesType, String detailValue) {
        this.categoryId = categoryId;
        this.clothesType = clothesType;
        this.detailValue = detailValue;
    }

    /**
     * 연관된 CategoryAllowedTypeEntity 연결 헬퍼
     */
    public void setAllowedType(CategoryAllowedTypeEntity allowedType) {
        this.allowedType = allowedType;
        this.allowedType.getAllowedDetails().add(this);
    }
}