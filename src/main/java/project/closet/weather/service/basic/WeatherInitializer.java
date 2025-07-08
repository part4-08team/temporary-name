package project.closet.weather.service.basic;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import project.closet.weather.repository.WeatherRepository;
import project.closet.weather.service.WeatherService;

@Slf4j
@Component
@RequiredArgsConstructor
public class WeatherInitializer implements ApplicationRunner {

    private final WeatherRepository weatherRepository;
    private final WeatherService weatherService;

    @Override
    public void run(ApplicationArguments args) {
        Instant forecastedAt = LocalDate.now()
                .minusDays(1)
                .atTime(23, 0)
                .atZone(ZoneId.of("Asia/Seoul"))
                .toInstant();

        boolean exists = weatherRepository.existsByForecastedAt(forecastedAt);

        if (!exists) {
            log.info("🌤️ 어제 23시 예보 데이터가 없어 초기 fetch 실행");
            weatherService.fetchAndSaveWeatherForecast();
            log.info("✅ 초기 날씨 데이터 저장 완료");
        } else {
            log.info("📦 forecastedAt {} 기준 데이터가 이미 존재합니다. 초기화 생략", forecastedAt);
        }
    }
}
