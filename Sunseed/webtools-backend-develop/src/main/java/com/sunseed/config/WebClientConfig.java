package com.sunseed.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

	// configuration webclient as bean
	@Bean
	WebClient getWebClientBuilder() {
		return WebClient.builder().build();
	}
}
