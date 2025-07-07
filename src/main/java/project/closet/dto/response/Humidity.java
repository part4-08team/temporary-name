package project.closet.dto.response;

public record Humidity(
        Double current,
        Double comparedToDayBefore
) {

}
