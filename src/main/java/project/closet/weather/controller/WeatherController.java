package project.closet.weather.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.dto.response.WeatherDto;
import project.closet.weather.controller.api.WeatherApi;
import project.closet.weather.service.WeatherService;
import project.closet.weather.service.basic.WeatherAPIClient;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/weathers")
public class WeatherController implements WeatherApi {

    private final WeatherService weatherService;
    private final WeatherAPIClient weatherAPIClient;

    @GetMapping
    @Override
    public ResponseEntity<List<WeatherDto>> getWeatherInfo(
            @RequestParam Double longitude,
            @RequestParam Double latitude
    ) {
        // TODO 날씨 정보 조회 요청
        log.info("날씨 정보 조회 요청: longitude={}, latitude={}", longitude, latitude);

        return ResponseEntity.ok(weatherService.getWeatherInfo(longitude, latitude));
    }

    @GetMapping("/location")
    @Override
    public ResponseEntity<WeatherAPILocation> getWeatherLocation(
            @RequestParam Double longitude,
            @RequestParam Double latitude
    ) {
        log.info("날씨 위치 정보 조회 요청: longitude={}, latitude={}", longitude, latitude);
        WeatherAPILocation location = weatherService.getLocation(longitude, latitude);
        return ResponseEntity.ok(location);
    }

}
