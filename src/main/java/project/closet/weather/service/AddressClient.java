package project.closet.weather.service;

import project.closet.dto.response.KakaoAddressResponse;

public interface AddressClient {

    KakaoAddressResponse requestAddressFromKakao(Double longitude, Double latitude);
}
