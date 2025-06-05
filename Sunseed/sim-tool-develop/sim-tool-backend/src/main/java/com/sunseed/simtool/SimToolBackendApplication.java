package com.sunseed.simtool;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sunseed.simtool.service.EPWService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableAsync
@Slf4j
@EnableConfigurationProperties
@EnableScheduling
public class SimToolBackendApplication {
	
	@Autowired
	private EPWService epwService;

	public static void main(String[] args) {
		SpringApplication.run(SimToolBackendApplication.class, args);
	}
	
	@EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() throws JsonMappingException, JsonProcessingException {
		log.info("Fetching weather data source (geojson) & saving in db ....");
		epwService.getGeoJsonData();
		log.info("Successfully fetched the weather data sources (geojson) & saved in db");
    }

}
