package project.closet.domain.recommend.service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesAttribute;
import project.closet.domain.clothes.entity.ClothesType;
import project.closet.domain.clothes.repository.ClothesRepository;
import project.closet.domain.recommend.dto.responses.ClothesForRecommendDto;
import project.closet.domain.recommend.dto.responses.RecommendationDto;
import project.closet.domain.recommend.entity.CategoryAllowedDetailEntity;
import project.closet.domain.recommend.entity.CategoryAllowedTypeEntity;
import project.closet.domain.recommend.entity.TemperatureCategoryEntity;
import project.closet.domain.recommend.repository.TemperatureCategoryRepository;
import project.closet.exception.user.UserNotFoundException;
import project.closet.exception.weather.WeatherNotFoundException;
import project.closet.security.ClosetUserDetails;
import project.closet.storage.S3ContentStorage;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.weather.entity.Weather;
import project.closet.weather.repository.WeatherRepository;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WeatherOutfitRecommendationServiceImpl implements WeatherOutfitRecommendationService {

    private static final double[] SENSITIVITY_OFFSETS = {
            -4.5,  // 0: 추위를 아주 많이 탐
            -3.0,  // 1: 추위를 좀 탐
            -1.5,  // 2: 표준
            1.5,  // 3: 더위를 좀 탐
            3.0,  // 4: 더위를 많이 탐
            4.5   // 5: 극한 더위 민감
    };

    private final WeatherRepository   weatherRepository;
    private final UserRepository      userRepository;
    private final ClothesRepository   clothesRepository;
    private final S3ContentStorage    s3ContentStorage;
    private final TemperatureCategoryRepository categoryRepo;

    @Override
    public RecommendationDto getRecommendationForWeather(UUID weatherId) {

        // 1) 날씨 조회 → 현재 기온만 사용
        Weather weather = weatherRepository.findById(weatherId)
                .orElseThrow(() -> WeatherNotFoundException.withId(weatherId));
        double currentTemp = weather.getCurrentTemperature();

        // 2) 요청을 보낸 사용자 id 추출
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }
        UUID userId = ((ClosetUserDetails) auth.getPrincipal()).getUserId();

        // 3) 사용자 + 프로필 조회 → 온도 민감도 확보
        User user = userRepository.findByIdWithProfile(userId)
                .orElseThrow(() -> UserNotFoundException.withId(userId));
        int sensitivity = user.getProfile().getTemperatureSensitivity() != null
                ? user.getProfile().getTemperatureSensitivity()
                : 0;

        // 4) Δ온도(offset) 테이블에서 꺼내기 (0~5 벗어나도 안전하게)
        int idx = Math.max(0, Math.min(SENSITIVITY_OFFSETS.length - 1, sensitivity));
        double offset = SENSITIVITY_OFFSETS[idx];

        // 4) 체감 온도 계산 (현재 온도 + 민감도)
        double adjustedTemp = currentTemp + offset;

        // 5) DB에서 카테고리 조회 (minTemp ≤ adjustedTemp < maxTemp)
        TemperatureCategoryEntity category = categoryRepo
                .findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(adjustedTemp, adjustedTemp)
                .orElseThrow(() -> new IllegalStateException("적절한 온도 구간을 찾을 수 없습니다."));

        // 5-1) DB 엔티티에서 allowedTypes·allowedDetails 맵핑
        Map<ClothesType, Set<String>> allowedDetailMap = category.getAllowedTypes().stream()
                .collect(Collectors.toMap(
                        CategoryAllowedTypeEntity::getClothesType,
                        t -> t.getAllowedDetails().stream()
                                .map(CategoryAllowedDetailEntity::getDetailValue)
                                .collect(Collectors.toSet())
                ));
        Set<ClothesType> allowedTypes = allowedDetailMap.keySet();

        // 6) 의상 전체 조회 → 대분류+세부속성 필터링
        List<Clothes> filtered = clothesRepository.findByOwnerId(userId).stream()
                .filter(c -> {
                    // 대분류 필터
                    if (!allowedTypes.contains(c.getType())) {
                        return false;
                    }
                    // 세부속성 필터
                    Set<String> allowedDetails = allowedDetailMap.get(c.getType());
                    if (allowedDetails == null || allowedDetails.isEmpty()) {
                        return true;
                    }
                    return c.getAttributes().stream()
                            .filter(a -> a.getDefinition().getDefinitionName().startsWith("의상 상세 종류"))
                            .map(ClothesAttribute::getValue)
                            .anyMatch(allowedDetails::contains);
                })
                .toList();

        // 7) 타입별 그룹핑 → 그룹당 하나의 아이템을 랜덤으로 선택 → ClothesDto 변환
        List<ClothesForRecommendDto> clothesList = filtered.stream()
                .collect(Collectors.groupingBy(Clothes::getType))
                .values().stream()
                .map(list -> {
                    // 필터링된 후보 중 랜덤 선택
                    Clothes pick = list.get(ThreadLocalRandom.current().nextInt(list.size()));
                    String imageUrl = s3ContentStorage.getPresignedUrl(pick.getImageKey());
                    return new ClothesForRecommendDto(pick, imageUrl);
                })
                .toList();

        // 8) 최종 DTO 반환
        return new RecommendationDto(weatherId, userId, clothesList);
    }
}
