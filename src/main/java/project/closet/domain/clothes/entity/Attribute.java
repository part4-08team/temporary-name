package project.closet.domain.clothes.entity;


import jakarta.persistence.*;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attribute {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    // 속성 정의명 (attributes.definition_name)
    @Column(name = "definition_name", nullable = false, unique = true, length = 50)
    private String definitionName;

    // ElementCollection 대신 OneToMany로 변경
    @OneToMany(
            mappedBy = "attribute",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AttributeSelectableValue> selectableValues = new ArrayList<>();

    public Attribute(String definitionName, List<String> values) {
        this.definitionName = definitionName;
        setSelectableValues(values);
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public void addSelectableValue(String value) {
        var sv = new AttributeSelectableValue(this, value);
        this.selectableValues.add(sv);
    }

    public void setSelectableValues(List<String> values) {
        this.selectableValues.clear();
        values.forEach(this::addSelectableValue);
    }

}
