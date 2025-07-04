package project.closet.weather.service.basic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import project.closet.dto.response.KakaoAddressResponse;
import project.closet.dto.response.KakaoAddressResponse.Document;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.weather.service.AddressClient;
import project.closet.weather.service.WeatherService;
import project.closet.weather.service.basic.GeoGridConverter.Grid;

@Slf4j
@Service
@RequiredArgsConstructor
public class BasicWeatherService implements WeatherService {

    private final AddressClient addressClient;
    private final GeoGridConverter geoGridConverter;

    @Override
    public WeatherAPILocation getLocation(Double longitude, Double latitude) {
        log.info("위도 경도로 행정구역 반환 요청: longitude={}, latitude={}", longitude, latitude);
        KakaoAddressResponse kakaoAddressResponse =
                addressClient.requestAddressFromKakao(longitude, latitude);
        Grid grid = geoGridConverter.convert(longitude, latitude);
        return new WeatherAPILocation(
                longitude,
                latitude,
                grid.x(),
                grid.y(),
                kakaoAddressResponse.getLocationNames()
        );
    }
}
