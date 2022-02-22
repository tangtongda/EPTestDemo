package com.temperature.service.impl;

import com.temperature.commons.exception.BizException;
import com.temperature.commons.utils.GsonUtil;
import com.temperature.commons.utils.OkHttpClientUtil;
import com.temperature.model.WeatherResponse;
import com.temperature.service.TPSLimitedService;
import com.temperature.service.TemperatureService;
import com.temperature.service.config.TemperatureProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TemperatureService interface implements
 *
 * @author <a href="mailto:tangtongda@gmail.com">Tino.Tang</a>
 * @version ${project.version} - 2022/2/22
 */
@Service
public class TemperatureServiceImpl implements TemperatureService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemperatureServiceImpl.class);

    /**
     * key->code,value->name
     */
    private Map<String, Object> provinceMap = new ConcurrentHashMap<>();

    @Autowired
    private TemperatureProperties temperatureProperties;

    @Autowired
    private TPSLimitedService tpsLimitedService;

    @Override
    public Optional<Integer> getTemperature(String province, String city, String county) {
        if (!tpsLimitedService.tryAcquire()) {
            throw new BizException("Service is busy now, please try again.");
        }
        // param nonNull validate
        if (StringUtils.isAnyBlank(province, city, county)) {
            throw new BizException("Params cannot be blank.");
        }
        // prince code
        Optional<String> provinceCode = getProvinceCode(province);
        if (!provinceCode.isPresent()) {
            LOGGER.error("Cannot find the province:{}", province);
            return Optional.empty();
        }
        // city code
        Optional<String> cityCode = getCityCode(provinceCode.get(), city);
        if (!cityCode.isPresent()) {
            LOGGER.error("Cannot find the city:{}", city);
            return Optional.empty();
        }
        // county code
        Optional<String> countyCode = getCountyCode(provinceCode.get() + cityCode.get(), county);
        if (!countyCode.isPresent()) {
            LOGGER.error("Cannot find the county:{}", county);
            return Optional.empty();
        }
        // return the nullable temperature
        return getTemperature(provinceCode.get() + cityCode.get() + cityCode.get());
    }

    /**
     * get provinceCode by province name
     *
     * @param province province name
     * @return provinceCode
     */
    private Optional<String> getProvinceCode(String province) {
        if (StringUtils.isBlank(province)) {
            throw new BizException("province cannot be blank.");
        }
        // get cache first
        if (!provinceMap.isEmpty()) {
            return Optional.ofNullable(getCodeFromMap(provinceMap, province));
        }
        // call to get province data
        String response = OkHttpClientUtil.get(temperatureProperties.getProvince());
        if (StringUtils.isBlank(response)) {
            throw new BizException("get province error: empty.");
        }
        provinceMap = GsonUtil.stringToMap(response);
        return Optional.ofNullable(getCodeFromMap(provinceMap, province));
    }

    /**
     * get cityCode by provinceCode
     *
     * @param provinceCode provinceCode
     * @param city         city name
     * @return cityCode
     */
    private Optional<String> getCityCode(String provinceCode, String city) {
        String response = OkHttpClientUtil.get(String.format(temperatureProperties.getCity(), provinceCode));
        Map<String, Object> map = GsonUtil.stringToMap(response);
        return Optional.ofNullable(getCodeFromMap(map, city));
    }

    /**
     * get countyCode by  provinceCityCode and county name
     *
     * @param provinceCityCode provinceCode+cityCode
     * @param county           county name
     * @return countyCode
     */
    private Optional<String> getCountyCode(String provinceCityCode, String county) {
        String response = OkHttpClientUtil.get(String.format(temperatureProperties.getCounty(), provinceCityCode));
        Map<String, Object> map = GsonUtil.stringToMap(response);
        return Optional.ofNullable(getCodeFromMap(map, county));
    }

    /**
     * get temperature by provinceCityCountyCode
     *
     * @param provinceCityCountyCode provinceCode+cityCode+CountyCode
     * @return temperature
     */
    private Optional<Integer> getTemperature(String provinceCityCountyCode) {
        String response = OkHttpClientUtil.get(String.format(temperatureProperties.getWeather(), provinceCityCountyCode));
        if (StringUtils.isBlank(response)) {
            throw new BizException("Cannot get any weather info.");
        }
        WeatherResponse weatherResponse;
        try {
            weatherResponse = GsonUtil.stringToBean(response, WeatherResponse.class);
        } catch (Exception e) {
            LOGGER.error("Get weather info error, response:{}", response);
            return Optional.empty();
        }
        WeatherResponse.Weatherinfo weatherinfo = weatherResponse.getWeatherinfo();
        if (null == weatherinfo) {
            throw new BizException("Cannot get any weather info.");
        }
        return Optional.of(Double.valueOf(weatherinfo.getTemp()).intValue());
    }


    /**
     * get  code from map
     *
     * @param map  code name map
     * @param name name
     * @return code
     */
    private String getCodeFromMap(Map<String, Object> map, String name) {
        for (String key : map.keySet()) {
            if (Objects.equals(map.get(key), name)) {
                return key;
            }
        }
        return null;
    }
}
