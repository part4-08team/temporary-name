package project.closet.domain.recommend.controller;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import project.closet.domain.recommend.dto.responses.RecommendationDto;
import project.closet.domain.recommend.service.WeatherOutfitRecommendationService;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final WeatherOutfitRecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationDto> getRecommendations(
            @RequestParam("weatherId") UUID weatherId
    ) {
        RecommendationDto dto =
                recommendationService.getRecommendationForWeather(weatherId);
        return ResponseEntity.ok(dto);
    }

}
