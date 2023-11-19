package org.example.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.example.Model.City;
import org.example.Model.User;
import org.example.Model.Weather;
import org.example.Repository.WeatherRepository;
import org.example.Service.CityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Date;
import java.util.List;

@Controller
public class WeatherController {

    private final CityService cityService;
    private final WebClient webClient;
    private final WeatherRepository weatherRepository;

    @Autowired
    public WeatherController(CityService cityService, WebClient.Builder webClientBuilder, WeatherRepository weatherRepository) {
        this.cityService = cityService;
        this.webClient = webClientBuilder.baseUrl("https://api.weather.yandex.ru").build();
        this.weatherRepository = weatherRepository;
    }

    @GetMapping("/weather")
    public String showWeatherPage(Model model) {
        List<City> cities = cityService.getAllCities();
        model.addAttribute("cities", cities);

        model.addAttribute("selectedCity", new City());

        return "weather";
    }

    @PostMapping("/weather")
    public String getWeather(@ModelAttribute("selectedCity") City selectedCity, Model model,  HttpSession session) {

        City cityFromDB = cityService.getCityById(selectedCity.getId());

        Double lat = cityFromDB.getLatitude();
        Double lon = cityFromDB.getLongitude();

        System.out.println("Selected City: " + cityFromDB.getCityName());
        System.out.println("Latitude: " + cityFromDB.getLatitude());
        System.out.println("Longitude: " + cityFromDB.getLongitude());

        String apiUrl = "/v2/informers?lat=" + lat + "&lon=" + lon + "&hours=true&limit=1&extra=false";

        String temperature = webClient.get()
                .uri(apiUrl)
                .header("X-Yandex-API-Key", "a055ede3-ddd9-43c1-b9d3-08bf71504984")
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> {
                    try {
                        ObjectMapper objectMapper = new ObjectMapper();
                        JsonNode rootNode = objectMapper.readTree(response);
                        return rootNode.path("fact").path("temp").asText();
                    } catch (Exception e) {
                        throw new RuntimeException("Error parsing JSON response", e);
                    }
                })
                .block(); // блокируем выполнение, чтобы получить результат синхронно

        model.addAttribute("temperature", temperature);


        // Сохраняем информацию о погоде в базу данных
        User user = (User) session.getAttribute("username");

        if (user != null) {
            Weather weather = new Weather();
            weather.setUsername(user.getUsername());
            weather.setCity_name(cityFromDB.getCityName());
            weather.setTemp(temperature);
            weather.setTimestamp(new Date());

            weatherRepository.save(weather);
        }


        return "weather";
    }

}
