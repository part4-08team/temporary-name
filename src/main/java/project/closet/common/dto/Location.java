package project.closet.common.dto;


import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Struct;
import org.hibernate.type.SqlTypes;

// todo : 불변 객체 vs 가변 객체  논의
@Embeddable
@Struct(name = "location_type")
public record Location(

    @Min(value = -90, message = "Latitude must be greater than -90")
    @Max(value = 90, message = "Latitude must be smaller than 90.")
    double latitude,

    @Min(value = -180, message = "Longitude must be greater than -180")
    @Max(value = 180, message = "Longitude must be smaller than 180.")
    double longitude,

    int x,
    int y,

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "location_names")
    List<String> locationNames
) {

}
