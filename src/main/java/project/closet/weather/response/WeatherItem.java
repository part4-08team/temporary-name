package project.closet.weather.response;

public record WeatherItem(
        String baseDate,   // 예보 발표 날짜
        String baseTime,   // 예보 발표 시각
        String category,   // 항목 코드 (TMP, SKY, etc)
        String fcstDate,   // 예보 날짜
        String fcstTime,   // 예보 시각
        String fcstValue,  // 값
        int nx,
        int ny
) {

}
