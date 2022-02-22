package com.temperature.service;

import java.util.Optional;

/**
 * TemperatureService interface
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
public interface TemperatureService {

    /**
     * get the temperature
     *
     * @param province chinese province
     * @param city     certain city
     * @param county   city county
     * @return county temperature
     */
    Optional<Integer> getTemperature(String province, String city, String county);
}
