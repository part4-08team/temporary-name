package project.closet.domain.clothes.entity;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.closet.domain.base.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "attributes")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Attribute extends BaseEntity {

    // 속성 정의명 (attributes.definition_name)
    @Column(name = "definition_name", nullable = false, unique = true, length = 50)
    private String definitionName;

    // attribute_selectable_value 테이블의 value 컬럼을 매핑
    @ElementCollection
    @CollectionTable(
            name = "attribute_selectable_value",
            joinColumns = @JoinColumn(name = "definition_id")
    )
    @Column(name = "value", nullable = false, length = 100)
    private List<String> selectableValues = new ArrayList<>();

    public Attribute(String definitionName, List<String> selectableValues) {
        this.definitionName    = definitionName;
        this.selectableValues  = new ArrayList<>(selectableValues);
    }

    public void setDefinitionName(String definitionName) {
        this.definitionName = definitionName;
    }

    public void setSelectableValues(List<String> values) {
        this.selectableValues = new ArrayList<>(values);
    }

}
