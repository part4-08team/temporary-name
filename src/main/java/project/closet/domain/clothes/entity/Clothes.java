package project.closet.domain.clothes.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.hibernate.annotations.BatchSize;
import project.closet.domain.base.BaseUpdatableEntity;
import project.closet.user.entity.User;

@Entity
@Table(
        name = "clothes",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_clothes_name",
                columnNames = "name"
        )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Clothes extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_key", nullable = false)
    private String imageKey;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ClothesType type;

    @BatchSize(size = 100)
    @OneToMany(
            mappedBy = "clothes",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ClothesAttribute> attributes = new ArrayList<>();

    public Clothes(
            User owner,
            String name,
            String imageKey,
            ClothesType type
    ) {
        this.owner = owner;
        this.name = name;
        this.imageKey = imageKey;
        this.type = type;
    }

    public void addAttribute(ClothesAttribute attribute) {
        attributes.add(attribute);
        attribute.setClothes(this);
    }

    public void removeAttribute(ClothesAttribute attribute) {
        attributes.remove(attribute);
        attribute.setClothes(null);
    }

    /** 이름·타입 업데이트 **/
    public void updateDetails(String name, ClothesType type) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
        if (type != null) {
            this.type = type;
        }
    }

    /** 이미지 Key 업데이트 **/
    public void updateImageKey(String imageKey) {
        this.imageKey = imageKey;
    }
}
