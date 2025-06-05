package com.sunseed.simtool.service;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sunseed.simtool.serviceimpl.EPWServiceImpl.WeatherData;

public interface EPWService {
    List<WeatherData> getWeatherInfoForDateTime(Double longitude, Double latitude) throws IOException;
    
    void getGeoJsonData() throws JsonMappingException, JsonProcessingException;
}

