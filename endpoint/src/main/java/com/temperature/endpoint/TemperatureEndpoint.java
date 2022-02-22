package com.temperature.endpoint;

import com.temperature.service.TemperatureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import java.util.Optional;

/**
 * template controller
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
@RestController
@RequestMapping("/temperature")
public class TemperatureEndpoint {

    @Autowired
    private TemperatureService temperatureService;

    /**
     * get temperature by location
     *
     * @param province province name
     * @param city     city name
     * @param county   county name
     * @return temperature
     */
    @GetMapping("/get")
    public Integer getTemperature(@RequestParam String province, @RequestParam String city, @RequestParam String county) {
        Optional<Integer> temperature = temperatureService.getTemperature(province, city, county);
        return temperature.orElse(null);
    }
}
