package com.roomie.server.domain.weather.presentation;

import com.roomie.server.domain.weather.application.WeatherService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/cleaning-advice")
    public String getCleaningAdvice() {
        return weatherService.getCleaningAdvice();
    }
}

