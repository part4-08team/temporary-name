package project.closet.domain.clothes.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "attribute_selectable_value")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AttributeSelectableValue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(
            columnDefinition = "uuid",
            updatable = false,
            nullable = false
    )
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "definition_id", nullable = false)
    private Attribute attribute;

    @Column(name = "value", nullable = false, length = 100)
    private String value;

    public AttributeSelectableValue(Attribute attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }
}