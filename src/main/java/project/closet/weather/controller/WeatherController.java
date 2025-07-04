package project.closet.weather.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.closet.weather.controller.api.WeatherApi;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/weathers")
public class WeatherController implements WeatherApi {

}
