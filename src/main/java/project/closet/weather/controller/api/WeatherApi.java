package project.closet.weather.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import project.closet.dto.response.WeatherAPILocation;
import project.closet.dto.response.WeatherDto;
import project.closet.exception.ErrorResponse;

@Tag(name = "날씨 관리", description = "날씨 관련 API")
public interface WeatherApi {

    @Operation(summary = "날씨 정보 조회", description = "날씨 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "날씨 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "날씨 정보 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<List<WeatherDto>> getWeatherInfo(
            @Parameter Double longitude,
            @Parameter Double latitude
    );

    @Operation(summary = "날씨 위치 정보 조회", description = "날씨 위치 정보 조회 API")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200", description = "날씨 위치 정보 조회 성공"
            ),
            @ApiResponse(
                    responseCode = "400", description = "날씨 위치 정보 조회 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    ResponseEntity<WeatherAPILocation> getWeatherLocation(
            @Parameter Double longitude,
            @Parameter Double latitude
    );
}
