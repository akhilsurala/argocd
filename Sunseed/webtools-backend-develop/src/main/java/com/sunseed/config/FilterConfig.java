package com.sunseed.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sunseed.filters.RequestDelegationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FilterConfig {

	private final RequestDelegationFilter requestDelegationFilter;

	@Bean
	public FilterRegistrationBean<RequestDelegationFilter> authorizationFilterRegistrationBean() {

		FilterRegistrationBean<RequestDelegationFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(requestDelegationFilter);
		registrationBean.addUrlPatterns("/*");
		return registrationBean;
	}
}
