package com.sunseed.simtool.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

	@Bean
	public ConcurrentHashMap<String, Future<?>> taskConcurrentHashMap()
	{
		return new ConcurrentHashMap<>();
	}
	
	@Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
