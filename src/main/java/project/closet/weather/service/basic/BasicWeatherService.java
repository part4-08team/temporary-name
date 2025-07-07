package project.closet.weather.service.basic;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
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
import project.closet.weather.kakaoresponse.WeatherApiResponse;
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

    @Transactional
    @Scheduled(cron = "0 0 23 * * *")  //매일 23시 0분 0초에 실행
    @Override
    public void fetchAndSaveWeatherForecast() {
        log.info("날씨 정보 처리 요청");
        LocalDate forecastBaseDate = LocalDate.now().minusDays(1);
        LocalTime forecastTime = LocalTime.of(23, 0);
        Instant forecastedAt = LocalDateTime.of(forecastBaseDate, forecastTime)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();

        weatherLocationRepository.findAll()
                .forEach(weatherLocation -> {
                    // 1. 데이터를 요청할 날짜 및 시간 설정 (총 6일 데이터가 필요함)
                    WeatherApiResponse weatherRawData = weatherAPIClient.getWeatherRawData(
                            weatherLocation.getX(),
                            weatherLocation.getY(),
                            forecastBaseDate,
                            forecastTime
                    );

                    // 2. 날씨 데이터 파싱
                    List<Weather> weathers = weatherDataParser.parseToWeatherEntities(
                            weatherRawData,
                            forecastedAt,
                            weatherLocation.getX(),
                            weatherLocation.getY()
                    );

                    // 3. DB 저장
                    if (!weathers.isEmpty()) {
                        weatherRepository.saveAll(weathers);
                        log.info("✅ {}건의 날씨 데이터 저장 완료 (x={}, y={})", weathers.size(), weatherLocation.getX(), weatherLocation.getY());
                    } else {
                        log.warn("⚠️ 파싱된 날씨 데이터 없음 (x={}, y={})", weatherLocation.getX(), weatherLocation.getY());
                    }
                });
        log.info("날씨 정보 처리 완료");
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
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();
        // 3. 날씨 정보 가공 후 반환
        List<Weather> weathers =
                weatherRepository.findAllByXAndYAndForecastedAt(grid.x(), grid.y(), baseForecastedAt);

        Map<Instant, Weather> weatherMapByForecastAt = weathers.stream()
                .collect(Collectors.toMap(Weather::getForecastAt, w -> w));

        return weathers.stream()
                .map(weather -> {
                    Weather yesterday = weatherMapByForecastAt.get(weather.getForecastAt().minus(1, ChronoUnit.DAYS));
                    return WeatherDto.from(weather, yesterday);
                })
                .toList();
    }
}
