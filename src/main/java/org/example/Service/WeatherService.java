package org.example.Service;

import org.example.Model.Weather;
import org.example.Repository.WeatherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WeatherService {

    @Autowired
    private WeatherRepository weatherRepository;

    public List<Weather> getWeatherHistoryByUsername(String username) {
        return weatherRepository.findByUsername(username);
    }
}
