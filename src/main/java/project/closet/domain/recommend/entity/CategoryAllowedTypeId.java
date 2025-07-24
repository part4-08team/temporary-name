package project.closet.domain.recommend.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import project.closet.domain.clothes.entity.ClothesType;

@NoArgsConstructor
@AllArgsConstructor
public class CategoryAllowedTypeId implements Serializable {
    private UUID categoryId;
    private ClothesType clothesType;  // JPA는 EnumType.STRING 매핑을 String으로 처리

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryAllowedTypeId that = (CategoryAllowedTypeId) o;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(clothesType, that.clothesType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, clothesType);
    }
}