package org.example.Repository;

import org.example.Model.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WeatherRepository extends JpaRepository<Weather, Long> {


    List<Weather> findByUsername(String username);
}
