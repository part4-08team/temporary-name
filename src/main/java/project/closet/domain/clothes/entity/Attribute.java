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

    public Attribute(String definitionName) {
        this.definitionName = definitionName;
    }

    // 선택값 추가 편의 메서드
    public void addSelectableValue(String value) {
        this.selectableValues.add(value);
    }

    // 선택값 전체 교체나 제거가 필요하면 아래 메서드도 추가할 수 있습니다.
    public void setSelectableValues(List<String> values) {
        this.selectableValues = new ArrayList<>(values);
    }
}
