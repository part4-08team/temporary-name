package project.closet.domain.clothes.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import project.closet.domain.base.BaseUpdatableEntity;

import java.util.ArrayList;
import java.util.List;
import project.closet.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "clothes",
        uniqueConstraints = @UniqueConstraint(name = "uk_clothes_name", columnNames = "name")
)
public class Clothes extends BaseUpdatableEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ClothesType type;

    @OneToMany(
            mappedBy = "clothes",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ClothesAttribute> attributes = new ArrayList<>();

    public Clothes(User owner, String name, String imageUrl, ClothesType type) {
        this.owner = owner;
        this.name = name;
        this.imageUrl = imageUrl;
        this.type = type;
    }

    // 양방향 관계 편의 메서드
    public void addAttribute(ClothesAttribute attribute) {
        attributes.add(attribute);
        attribute.setClothes(this);
    }

    public void removeAttribute(ClothesAttribute attribute) {
        attributes.remove(attribute);
        attribute.setClothes(null);
    }
}
