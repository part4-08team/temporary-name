package project.closet.weather.kakaoresponse;

public record Body(String dataType, Items items, int pageNo, int numOfRows, int totalCount) {

}
