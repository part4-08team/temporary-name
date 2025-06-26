package project.closet.common.dto;


import jakarta.persistence.Embeddable;
import java.util.List;
import org.hibernate.annotations.Struct;

// todo : 불변 객체로 할지 수정 가능할게 할지 상의
@Embeddable
@Struct(name = "location_type", attributes = {"latitude", "longitude", "x", "y", "locationName"})
public record Location(
    double latitude,
    double longitude,
    int x,
    int y,
    List<String> locationName
) {
}
