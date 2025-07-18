package project.closet.domain.recommend.controller;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import project.closet.domain.clothes.dto.response.ClothesDto;
import project.closet.domain.recommend.dto.responses.RecommendationDto;
import project.closet.domain.recommend.service.WeatherOutfitRecommendationService;

class RecommendationControllerTest {

    private WeatherOutfitRecommendationService svc;
    private RecommendationController ctrl;

    @BeforeEach
    void setUp() {
        svc = mock(WeatherOutfitRecommendationService.class);
        ctrl = new RecommendationController(svc);
    }

    @Test
    void getRecommendations_ShouldReturnDto() {
        UUID weatherId = UUID.randomUUID();
        UUID userId    = UUID.randomUUID();
        ClothesDto dto1 = new ClothesDto(UUID.randomUUID(), userId, "Top", "url", "TOP", List.of());
        RecommendationDto expected = new RecommendationDto(weatherId, userId, List.of(dto1));

        when(svc.getRecommendationForWeather(weatherId)).thenReturn(expected);

        var response = ctrl.getRecommendations(weatherId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isSameAs(expected);

        verify(svc).getRecommendationForWeather(weatherId);
    }
}
