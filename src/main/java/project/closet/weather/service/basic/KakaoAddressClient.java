package project.closet.weather.service.basic;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import project.closet.dto.response.KakaoAddressResponse;
import project.closet.dto.response.KakaoAddressResponse.Document;
import project.closet.weather.service.AddressClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class KakaoAddressClient implements AddressClient {

    @Value("${closet.kakao.api.key}")
    private String kakaoApiKey;

    private final RestTemplate restTemplate;

    @Override
    public KakaoAddressResponse requestAddressFromKakao(Double longitude, Double latitude) {
        String url =
                "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x="
                        + longitude
                        + "&y=" + latitude;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "KakaoAK " + kakaoApiKey);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<KakaoAddressResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, KakaoAddressResponse.class);
            KakaoAddressResponse body = response.getBody();
            List<Document> documents = body.getDocuments();

            if (!documents.isEmpty()) {
                KakaoAddressResponse.Document doc = documents.get(0);
                log.debug("Kakao 응답 데이터 = 시/도: {}, 시/군/구: {}, 읍/면/동: {}",
                        doc.getRegion_1depth_name(),
                        doc.getRegion_2depth_name(),
                        doc.getRegion_3depth_name());
            }
            return body;
        } catch (Exception e) {
            log.error("Kakao API 호출 실패: {}", e.getMessage());
            throw new RuntimeException("Kakao 주소 API 호출 실패", e);
        }
    }
}
