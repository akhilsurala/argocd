package com.sunseed.simtool.config;

import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RequestLoggingFilterConfig extends AbstractRequestLoggingFilter{

	@Override
	protected void beforeRequest(HttpServletRequest request, String message) {
		MDC.put("uuid", UUID.randomUUID().toString());
	}

	@Override
	protected void afterRequest(HttpServletRequest request, String message) {
		MDC.remove("uuid");
	}
	
}
