package com.sunseed;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.net.http.HttpClient;

@SpringBootApplication
public class SunseedAgriPVApplication {

	public static void main(String[] args) {
		SpringApplication.run(SunseedAgriPVApplication.class, args);

	}

	@Bean
	ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	HttpClient httpClient() {
		return HttpClient.newBuilder().build();
	}

}
