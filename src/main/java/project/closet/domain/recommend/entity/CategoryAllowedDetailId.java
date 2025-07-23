package project.closet.domain.recommend.entity;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import project.closet.domain.clothes.entity.ClothesType;

@NoArgsConstructor
@AllArgsConstructor
public class CategoryAllowedDetailId implements Serializable {
    private UUID categoryId;
    private ClothesType clothesType;
    private String detailValue;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoryAllowedDetailId that = (CategoryAllowedDetailId) o;
        return Objects.equals(categoryId, that.categoryId) &&
                Objects.equals(clothesType, that.clothesType) &&
                Objects.equals(detailValue, that.detailValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(categoryId, clothesType, detailValue);
    }
}