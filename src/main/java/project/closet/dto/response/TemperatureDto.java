package project.closet.dto.response;

public record TemperatureDto(
        Double current,
        Double comparedToDayBefore,
        Double min,
        Double max
) {

}
