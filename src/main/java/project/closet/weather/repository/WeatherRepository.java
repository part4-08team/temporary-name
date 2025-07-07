package project.closet.weather.repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import project.closet.weather.entity.Weather;

public interface WeatherRepository extends JpaRepository<Weather, UUID> {

    List<Weather> findAllByXAndYAndForecastedAt(Integer x, Integer y, Instant forecastedAt);

    boolean existsByForecastedAt(Instant forecastedAt);
}
