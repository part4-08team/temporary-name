package project.closet.common.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Struct;
import org.hibernate.type.SqlTypes;

// todo : 불변 객체 vs 가변 객체  논의
@Embeddable
@Struct(name = "location_type")
public record Location(

    @NotBlank(message = "위도를 입력해주세요.")
    double latitude,

    @NotBlank(message = "경도를 입력해주세요.")
    double longitude,

    @NotBlank(message = "x좌표를 입력해주세요.")
    int x,

    @NotBlank(message = "y좌표를 입력해주세요.")
    int y,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "location_names")
    List<String> locationNames
) {

}
