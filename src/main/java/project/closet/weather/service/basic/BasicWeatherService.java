package project.closet.weather.service.basic;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.dto.response.KakaoAddressResponse;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.dto.response.WeatherDto;
import project.closet.weather.entity.Weather;
import project.closet.weather.location.WeatherLocation;
import project.closet.weather.location.WeatherLocationRepository;
import project.closet.weather.repository.WeatherRepository;
import project.closet.weather.service.AddressClient;
import project.closet.weather.service.WeatherService;
import project.closet.weather.service.basic.GeoGridConverter.Grid;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicWeatherService implements WeatherService {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final AddressClient addressClient;
    private final WeatherRepository weatherRepository;
    private final WeatherLocationRepository weatherLocationRepository;

    private final GeoGridConverter geoGridConverter;
    private final WeatherAPIClient weatherAPIClient;
    private final WeatherDataParser weatherDataParser;

    @Transactional(readOnly = true)
    @Override
    public WeatherAPILocation getLocation(Double longitude, Double latitude) {
        log.info("위도 경도로 행정구역 반환 요청: longitude={}, latitude={}", longitude, latitude);
        KakaoAddressResponse kakaoAddressResponse =
                addressClient.requestAddressFromKakao(longitude, latitude);
        Grid grid = geoGridConverter.convert(latitude, longitude);
        return new WeatherAPILocation(
                latitude,
                longitude,
                grid.x(),
                grid.y(),
                kakaoAddressResponse.getLocationNames()
        );
    }

    @Override
    @Scheduled(cron = "0 55 23 * * *", zone = "Asia/Seoul")
    public void fetchAndSaveWeatherForecast() {
        LocalDate forecastBaseDate = LocalDate.now();
        LocalTime forecastTime = LocalTime.of(23, 0);
        Instant forecastedAt = LocalDateTime.of(forecastBaseDate, forecastTime)
                .atZone(SEOUL)
                .toInstant();

        fetchAndSave(forecastBaseDate, forecastTime, forecastedAt);
    }

    @Override
    public void fetchAndSave(LocalDate baseDate, LocalTime forecastTime, Instant forecastedAt) {
        log.info("🌤️ 날씨 정보 처리 요청: baseDate={}, forecastTime={}", baseDate, forecastTime);

        List<WeatherLocation> locations = weatherLocationRepository.findAll();
        int batchSize = 100;  // 원하는 batch 크기

        for (int i = 0; i < locations.size(); i += batchSize) {
            List<WeatherLocation> batch = locations.subList(i, Math.min(i + batchSize, locations.size()));
            log.info("🚀 {}~{}번째 지역 날씨 요청 시작", i + 1, Math.min(i + batchSize, locations.size()));

            List<CompletableFuture<List<Weather>>> futures = batch.stream()
                    .map(location -> weatherAPIClient.fetchWeatherAsync(location.getX(), location.getY(), baseDate, forecastTime)
                            .thenApply(response ->
                                    weatherDataParser.parseToWeatherEntities(response, forecastedAt, location.getX(), location.getY())
                            )
                            .exceptionally(ex -> {
                                log.warn("❌ 날씨 요청 실패 (x={}, y={}): {}", location.getX(), location.getY(), ex.getMessage());
                                return Collections.emptyList();
                            })
                    ).toList();

            // 이 batch가 완료될 때까지 기다림
            List<Weather> parsedWeather = futures.stream()
                    .map(CompletableFuture::join)
                    .flatMap(List::stream)
                    .collect(Collectors.toList());

            if (!parsedWeather.isEmpty()) {
                weatherRepository.saveAll(parsedWeather);
                log.info("✅ 배치 {}건 저장 완료", parsedWeather.size());
            } else {
                log.warn("⚠️ 저장할 데이터 없음 ({}~{})", i + 1, i + batchSize);
            }

            // 선택: 서버 과부하 방지용 sleep (필요시)
            try {
                Thread.sleep(1000);  // 1초 쉬었다가 다음 배치 실행
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        log.info("🌤️ 날씨 정보 전체 처리 완료");
    }


    // TODO 바로 전 날짜의 온도와 비교해서 온도 정보 반환
    @Transactional(readOnly = true)
    @Override
    public List<WeatherDto> getWeatherInfo(Double longitude, Double latitude) {
        // 1. 위도 경도 -> X,Y 좌표 변환
        Grid grid = geoGridConverter.convert(latitude, longitude);
        // 2. 변환된 XY 좌표로 날씨 데이터 호출
        LocalDate forecastDate = LocalDate.now().minusDays(1);
        LocalTime forecastTime = LocalTime.of(23, 0);
        Instant baseForecastedAt = LocalDateTime.of(forecastDate, forecastTime)
                .atZone(SEOUL)
                .toInstant();
        // 3. 날씨 정보 가공 후 반환
        List<Weather> weathers =
                weatherRepository.findAllByXAndYAndForecastedAtOrderByForecastAtAsc(grid.x(),
                        grid.y(), baseForecastedAt);

        Map<Instant, Weather> weatherMapByForecastAt = weathers.stream()
                .collect(Collectors.toMap(Weather::getForecastAt, w -> w));

        return weathers.stream()
                .map(weather -> {
                    Weather yesterday = weatherMapByForecastAt.get(
                            weather.getForecastAt().minus(1, ChronoUnit.DAYS));
                    return WeatherDto.from(weather, yesterday);
                })
                .toList();
    }
}
