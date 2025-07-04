package project.closet.weather.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.weather.controller.api.WeatherApi;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/weathers")
public class WeatherController implements WeatherApi {

    @GetMapping
    @Override
    public ResponseEntity<List<String>> getWeatherInfo(
            Double longitude,
            Double latitude
    ) {
        return null;
    }

    @GetMapping("/location")
    @Override
    public ResponseEntity<WeatherAPILocation> getWeatherLocation(
            Double longitude,
            Double latitude
    ) {
        return null;
    }
}
