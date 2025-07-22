package project.closet.weather.service.basic;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import project.closet.weather.entity.AsWord;
import project.closet.weather.entity.PrecipitationType;
import project.closet.weather.entity.SkyStatus;
import project.closet.weather.entity.Weather;
import project.closet.weather.kakaoresponse.WeatherApiResponse;
import project.closet.weather.kakaoresponse.WeatherItem;

@Slf4j
@Component
public class WeatherDataParser {

    private static final ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");

    public List<Weather> parseToWeatherEntities(
            WeatherApiResponse response,
            Instant forecastedAt,
            int x,
            int y
    ) {

        if (response == null || response.response() == null
                || response.response().body() == null
                || response.response().body().items() == null) {
            log.warn("⛔ 잘못된 날씨 응답 데이터입니다.");
            return List.of();
        }

        List<WeatherItem> items = response.response().body().items().item();

        // ✅ TMN (최저기온, 06:00) 및 TMX (최고기온, 15:00) 데이터 매핑
        Map<LocalDate, Double> minTempMap = extractTemperatureMap(items, "TMN", "0600");
        Map<LocalDate, Double> maxTempMap = extractTemperatureMap(items, "TMX", "1500");

        // 습도 평균을 구하는 메소드 리팩토링 -> 모든 시간대의 습도를 각각 매핑
        Map<LocalDate, Double> humidityMap = computeAverageHumidityByDate(items);
        Map<LocalDate, Double> popMap = computeAveragePrecipitationProbabilityByDate(items);
        Map<LocalDate, Double> windSpeedMap = computeAverageWindSpeedByDate(items);

        // 00시 데이터만 필터링 → 날짜별 그룹화
        Map<LocalDate, List<WeatherItem>> grouped = items.stream()
                .filter(item -> "0000".equals(item.fcstTime()))
                .collect(Collectors.groupingBy(
                        i -> LocalDate.parse(i.fcstDate(), DateTimeFormatter.BASIC_ISO_DATE)));

        // 날짜별 Weather 생성
        List<Weather> weathers = new ArrayList<>();
        for (Map.Entry<LocalDate, List<WeatherItem>> entry : grouped.entrySet()) {
            LocalDate date = entry.getKey();
            List<WeatherItem> dailyItems = entry.getValue();

            // ⚠️ TMN/TMX 데이터 없는 날짜 제외
            if (!minTempMap.containsKey(date) || !maxTempMap.containsKey(date)) {
                log.debug("⏭️ TMN/TMX 누락 → {}일 데이터 생략", date);
                continue;
            }

            Instant forecastAt = date.atStartOfDay()
                    .atZone(ZONE_ID)
                    .toInstant();

            Weather weather = Weather.builder()
                    .forecastedAt(forecastedAt)
                    .forecastAt(forecastAt)
                    .skyStatus(mapSky(dailyItems))
                    .precipitationType(mapPty(dailyItems))
                    .amount(mapPcp(dailyItems))
                    .probability(popMap.getOrDefault(date, 0.0))
                    .humidity(humidityMap.getOrDefault(date, 0.0))
                    .windSpeed(windSpeedMap.getOrDefault(date, 0.0))
                    .asWord(convertToWindWord(windSpeedMap.getOrDefault(date, 0.0)))
                    .currentTemperature(mapDoubleValue(dailyItems, "TMP"))
                    .minTemperature(minTempMap.get(date))
                    .maxTemperature(maxTempMap.get(date))
                    .x(x)
                    .y(y)
                    .build();

            weathers.add(weather);
        }

        return weathers;
    }

    private Map<LocalDate, Double> extractTemperatureMap(
            List<WeatherItem> items,
            String category,
            String fcstTime
    ) {
        return items.stream()
                .filter(item -> category.equals(item.category()) && fcstTime.equals(
                        item.fcstTime()))
                .collect(Collectors.toMap(
                        item -> LocalDate.parse(item.fcstDate(), DateTimeFormatter.BASIC_ISO_DATE),
                        item -> parseTemperature(item.fcstValue()),
                        (a, b) -> a // 중복 방지
                ));
    }

    private Double parseTemperature(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            log.warn("⛔ 온도 파싱 실패: {}", value);
            return 0.0;
        }
    }

    // 하늘 상태를 SKY 카테고리에서 가져와서 SkyStatus로 매핑
    private SkyStatus mapSky(List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "SKY".equals(item.category()))
                .map(WeatherItem::fcstValue)
                .findFirst()
                .map(value -> {
                    return switch (value) {
                        case "1" -> SkyStatus.CLEAR;
                        case "3" -> SkyStatus.MOSTLY_CLOUDY;
                        case "4" -> SkyStatus.CLOUDY;
                        default -> SkyStatus.UNKNOWN;
                    };
                })
                .orElse(SkyStatus.UNKNOWN);
    }

    // 강수 형태를 PTY 카테고리에서 가져와서 precipitationType 으로 매핑
    private PrecipitationType mapPty(List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "PTY".equals(item.category()))
                .map(WeatherItem::fcstValue)
                .findFirst()
                .map(value -> {
                    return switch (value) {
                        case "0" -> PrecipitationType.NONE;
                        case "1" -> PrecipitationType.RAIN;
                        case "2" -> PrecipitationType.RAIN_SNOW;
                        case "3" -> PrecipitationType.SNOW;
                        case "4" -> PrecipitationType.SHOWER;
                        default -> PrecipitationType.UNKNOWN;
                    };
                })
                .orElse(PrecipitationType.UNKNOWN);
    }

    private Double mapPcp(List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "PCP".equals(item.category()))
                .map(WeatherItem::fcstValue)
                .findFirst()
                .map(this::parsePrecipitation)
                .orElse(0.0); // 해당 카테고리가 없을 경우 0으로 처리
    }

    private Double parsePrecipitation(String value) {
        if (value.contains("강수없음")) {
            return 0.0;
        }
        if (value.contains("1mm 미만")) {
            return 0.1;
        }

        // "30.0~50.0mm" 처리
        if (value.contains("~")) {
            String[] parts = value.replace("mm", "").split("~");
            try {
                double start = Double.parseDouble(parts[0].trim());
                double end = Double.parseDouble(parts[1].trim());
                return (start + end) / 2;
            } catch (NumberFormatException e) {
                log.warn("⛔ 강수량 범위 파싱 실패: {}", value);
                return 0.0;
            }
        }

        // "50.0mm 이상" → 고정 상한값 설정
        if (value.contains("이상")) {
            String numeric = value.replace("mm", "")
                    .replace("이상", "")
                    .trim();
            try {
                return Double.parseDouble(numeric);
            } catch (NumberFormatException e) {
                log.warn("⛔ 강수량 이상값 파싱 실패: {}", value);
                return 0.0;
            }
        }

        // "1.5mm" 등 일반적인 실수형
        try {
            return Double.parseDouble(value.replace("mm", "").trim());
        } catch (NumberFormatException e) {
            log.warn("⛔ 일반 강수량 파싱 실패: {}", value);
            return 0.0;
        }
    }

    private AsWord convertToWindWord(double speed) {
        if (speed < 4.0) {
            return AsWord.WEAK;
        }
        if (speed < 9.0) {
            return AsWord.MODERATE;
        }
        return AsWord.STRONG; // 14.0 이상은 STRONG 처리
    }

    private double mapDoubleValue(List<WeatherItem> items, String category) {
        return items.stream()
                .filter(i -> category.equals(i.category()))
                .map(WeatherItem::fcstValue)
                .findFirst()
                .map(Double::parseDouble)
                .orElse(0.0);
    }

    // 습도 평균 구하는 메소드
    private Map<LocalDate, Double> computeAverageHumidityByDate(List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "REH".equals(item.category()))
                .collect(Collectors.groupingBy(
                        item -> LocalDate.parse(item.fcstDate(), DateTimeFormatter.BASIC_ISO_DATE),
                        Collectors.averagingDouble(item -> {
                            try {
                                return Double.parseDouble(item.fcstValue());
                            } catch (NumberFormatException e) {
                                return 0.0; // 또는 로그 출력
                            }
                        })
                ));
    }

    // 강수 확률 평균
    private Map<LocalDate, Double> computeAveragePrecipitationProbabilityByDate(
            List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "POP".equals(item.category()))
                .collect(Collectors.groupingBy(
                        item -> LocalDate.parse(item.fcstDate(), DateTimeFormatter.BASIC_ISO_DATE),
                        Collectors.averagingDouble(item -> {
                            try {
                                return Double.parseDouble(item.fcstValue());
                            } catch (NumberFormatException e) {
                                return 0.0; // 또는 log.warn(...)
                            }
                        })
                ));
    }

    private Map<LocalDate, Double> computeAverageWindSpeedByDate(List<WeatherItem> items) {
        return items.stream()
                .filter(item -> "WSD".equals(item.category()))
                .collect(Collectors.groupingBy(
                        item -> LocalDate.parse(item.fcstDate(), DateTimeFormatter.BASIC_ISO_DATE),
                        Collectors.averagingDouble(item -> {
                            try {
                                return Double.parseDouble(item.fcstValue());
                            } catch (NumberFormatException e) {
                                return 0.0;  // 또는 log.warn(...);
                            }
                        })
                ));
    }

}
