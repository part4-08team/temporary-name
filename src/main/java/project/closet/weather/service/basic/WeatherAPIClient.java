package project.closet.weather.service.basic;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import project.closet.weather.kakaoresponse.WeatherApiResponse;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherAPIClient {

    @Value("${closet.weather.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherApiResponse getWeatherRawData(int x, int y, LocalDate baseDate, LocalTime baseTime) {
        String formattedDate = baseDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String formattedTime = baseTime.format(DateTimeFormatter.ofPattern("HHmm"));
        URI uri = buildUri(x, y, formattedDate, formattedTime);

        log.debug("Weather API 호출 URI: {}", uri);
        WeatherApiResponse response = restTemplate.getForObject(uri, WeatherApiResponse.class);
//        log.debug("Weather API 응답: {}", response);
        return response;
    }

    @Async("weatherExecutor")
    public CompletableFuture<WeatherApiResponse> fetchWeatherAsync(int x, int y, LocalDate baseDate, LocalTime baseTime) {
        return CompletableFuture.completedFuture(
                getWeatherRawData(x, y, baseDate, baseTime)
        );
    }

    public URI buildUri(int x, int y, String baseDate, String baseTime) {
        return UriComponentsBuilder.fromUriString("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst")
                .queryParam("ServiceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 2000)
                .queryParam("dataType", "JSON")
                .queryParam("base_date", baseDate)
                .queryParam("base_time", baseTime)
                .queryParam("nx", x)
                .queryParam("ny", y)
                .build(true) // true면 인코딩 처리
                .toUri();
    }
}
