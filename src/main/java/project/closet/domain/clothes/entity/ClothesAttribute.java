package project.closet.domain.clothes.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "clothes_attributes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ClothesAttribute {

    @Id
//    @Column(name = "clothes_attributes_id", columnDefinition = "uuid")
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "clothes_id", nullable = false)
    private Clothes clothes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false)
    private Attribute definition;

    @Column(name = "value", nullable = false, length = 50)
    private String value;

    public ClothesAttribute(Attribute definition, String value) {
        this.id         = UUID.randomUUID();
        this.definition = definition;
        this.value      = value;
    }

    void setClothes(Clothes clothes) {
        this.clothes = clothes;
    }
}
