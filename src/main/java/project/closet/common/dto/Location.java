package project.closet.common.dto;


import jakarta.persistence.Embeddable;
import java.util.List;
import org.hibernate.annotations.Struct;

@Embeddable
@Struct(name = "location_type")
public record Location(
    double latitude,
    double longitude,
    int x,
    int y,
    List<String> locationName
) {
}
