package project.closet.domain.clothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import project.closet.domain.base.BaseEntity;


@Entity
@Table(name = "clothes_attributes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothesAttribute extends BaseEntity {

    // clothes_attributes.clothes_id → Clothes 엔티티와 연관
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    // clothes_attributes.definition_id → Attribute 엔티티와 연관
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false)
    private Attribute definition;

    // clothes_attributes.value 컬럼
    @Column(nullable = false, length = 50)
    private String value;

    public ClothesAttribute(Attribute definition, String value) {
        this.definition = definition;
        this.value = value;
    }

    // 양방향 편의 메서드
    void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }
}