package project.closet.weather.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "weather_locations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"x", "y"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class WeatherLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private int x;

    @Column(nullable = false)
    private int y;

    @Builder
    public WeatherLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
