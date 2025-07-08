package project.closet.weather.response;

public record Body(String dataType, Items items, int pageNo, int numOfRows, int totalCount) {

}
