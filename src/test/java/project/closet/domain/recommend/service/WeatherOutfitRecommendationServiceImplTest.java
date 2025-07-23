package project.closet.domain.recommend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import project.closet.domain.clothes.entity.Clothes;
import project.closet.domain.clothes.entity.ClothesAttribute;
import project.closet.domain.clothes.entity.ClothesType;
import project.closet.domain.clothes.repository.ClothesRepository;
import project.closet.domain.recommend.dto.responses.RecommendationDto;
import project.closet.domain.recommend.entity.CategoryAllowedDetailEntity;
import project.closet.domain.recommend.entity.CategoryAllowedTypeEntity;
import project.closet.domain.recommend.entity.TemperatureCategoryEntity;
import project.closet.domain.recommend.repository.TemperatureCategoryRepository;
import project.closet.storage.S3ContentStorage;
import project.closet.user.entity.User;
import project.closet.user.repository.UserRepository;
import project.closet.weather.entity.Weather;
import project.closet.weather.repository.WeatherRepository;
import project.closet.security.ClosetUserDetails;

@SpringBootTest(classes = WeatherOutfitRecommendationServiceImpl.class)
class WeatherOutfitRecommendationServiceImplTest {

    @MockitoBean
    private WeatherRepository weatherRepo;

    @MockitoBean
    private UserRepository userRepo;

    @MockitoBean
    private ClothesRepository clothesRepo;

    @MockitoBean
    private S3ContentStorage s3;

    @MockitoBean
    private TemperatureCategoryRepository categoryRepo;

    @Autowired
    private WeatherOutfitRecommendationServiceImpl service;

    private final UUID dummyWeatherId = UUID.randomUUID();
    private final UUID dummyUserId    = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        // 0) SecurityContextHolder에 모킹한 Authentication 주입
        Authentication auth = mock(Authentication.class);
        given(auth.isAuthenticated()).willReturn(true);

        // principal로 ClosetUserDetails 반환
        ClosetUserDetails cud = mock(ClosetUserDetails.class);
        given(cud.getUserId()).willReturn(dummyUserId);
        given(auth.getPrincipal()).willReturn(cud);

        SecurityContextHolder.getContext().setAuthentication(auth);

        // 1) 날씨 조회 stub
        Weather w = Mockito.mock(Weather.class);
        given(w.getCurrentTemperature()).willReturn(20.0);
        given(weatherRepo.findById(dummyWeatherId))
                .willReturn(Optional.of(w));

        // 2) 사용자+프로필 stub
        User u = Mockito.mock(User.class);
        var profile = Mockito.mock(project.closet.user.entity.Profile.class);
        given(profile.getTemperatureSensitivity()).willReturn(2);
        given(u.getProfile()).willReturn(profile);
        given(userRepo.findByIdWithProfile(dummyUserId))
                .willReturn(Optional.of(u));

        // 3) 카테고리 리포지토리 stub
        TemperatureCategoryEntity cat = new TemperatureCategoryEntity("COOL", 17, 20);
        CategoryAllowedTypeEntity typeEnt =
                new CategoryAllowedTypeEntity(cat.getId(), ClothesType.TOP);
        CategoryAllowedDetailEntity detEnt =
                new CategoryAllowedDetailEntity(cat.getId(), ClothesType.TOP, "긴팔");
        typeEnt.addAllowedDetail(detEnt);
        cat.addAllowedType(typeEnt);
        given(categoryRepo.findFirstByMinTempLessThanEqualAndMaxTempGreaterThan(
                anyDouble(), anyDouble()))
                .willReturn(Optional.of(cat));

        // 4) clothesRepo stub
        Clothes c = Mockito.mock(Clothes.class);
        // owner 설정
        User owner = Mockito.mock(User.class);
        given(owner.getId()).willReturn(dummyUserId);
        given(c.getOwner()).willReturn(owner);

        given(c.getType()).willReturn(ClothesType.TOP);
        ClothesAttribute attr = mock(ClothesAttribute.class, RETURNS_DEEP_STUBS);
        given(attr.getDefinition().getDefinitionName())
                .willReturn("의상 상세 종류: 긴팔");
        given(attr.getValue()).willReturn("긴팔");
        given(c.getAttributes()).willReturn(List.of(attr));

        given(c.getImageKey()).willReturn("key");
        given(clothesRepo.findByOwnerId(dummyUserId))
                .willReturn(List.of(c));

        // 5) S3 stub
        given(s3.getPresignedUrl("key")).willReturn("http://url");
    }

    @Test
    void testServiceReturnsDtoWithMatchingClothes() {
        RecommendationDto dto = service.getRecommendationForWeather(dummyWeatherId);

        assertThat(dto.userId()).isEqualTo(dummyUserId);
        assertThat(dto.clothes())
                .allMatch(cd ->
                        cd.type().equals(ClothesType.TOP.name()) &&
                                cd.imageUrl().equals("http://url")
                );
    }
}
