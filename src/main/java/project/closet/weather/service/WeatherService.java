package project.closet.weather.service;

import project.closet.dto.response.WeatherAPILocation;

public interface WeatherService {

    // 위도 경도로 행정구역 반환해주는 메소드
    WeatherAPILocation getLocation(Double longitude, Double latitude);
}
