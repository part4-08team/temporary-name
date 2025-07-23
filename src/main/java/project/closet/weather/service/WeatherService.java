package project.closet.weather.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.dto.response.WeatherDto;

public interface WeatherService {

    // 위도 경도로 행정구역 반환해주는 메소드
    WeatherAPILocation getLocation(Double longitude, Double latitude);

    // 스케줄러로 날씨 정보 저장 메소드
    void fetchAndSaveWeatherForecast();

    void fetchAndSave(LocalDate baseDate, LocalTime forecastTime, Instant forecastedAt);

    // 날씨 정보 조회 요청 메소드
    List<WeatherDto> getWeatherInfo(Double longitude, Double latitude);
}
