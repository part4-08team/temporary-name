package project.closet.domain.recommend.service;

import java.util.UUID;
import project.closet.domain.recommend.dto.responses.RecommendationDto;

public interface WeatherOutfitRecommendationService {

    RecommendationDto getRecommendationForWeather(UUID weatherId);

}
