package com.sunseed.helper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.RequestMethod;

import com.sunseed.enums.EndpointStatus;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UriMatcherHelper {

	private final EndpointListProvider endpointListProvider;
	private static final Map<String, List<String>> allowedEndpoints = getAllowedEndpoints();

	public EndpointStatus getEndpointStatus(String requestURI, String requestMethod) {
		boolean isUriMatched = isUriMatched(requestURI, requestMethod);
		if (isUriMatched == false)
			return EndpointStatus.NOT_FOUND;
		else {
			boolean isAllowedEndpoint = isAllowedEndpoint(requestURI, requestMethod);
			if (isAllowedEndpoint == true)
				return EndpointStatus.ALLOWED;
			return EndpointStatus.AUTH_REQUIRED;
		}
	}

	private boolean isUriMatched(String requestURI, String requestMethod) {
		final Map<String, List<RequestMethod>> registeredEndpoints = endpointListProvider.getEndpointMethodsMap();
		AntPathMatcher pathMatcher = new AntPathMatcher();
		return registeredEndpoints.entrySet().stream().filter(entry -> {
			String pattern = entry.getKey();
			return !pattern.contains("${") && pathMatcher.match(pattern, requestURI);
		}).flatMap(entry -> entry.getValue().stream()).anyMatch(method -> method.name().equals(requestMethod));
	}

	// Checking if the request URI is among the allowed endpoints and method is
	// allowed
	private boolean isAllowedEndpoint(String requestURI, String requestMethod) {

		if (requestURI.contains("/swagger-ui") || requestURI.contains("/api-docs"))
			return true;
		if (allowedEndpoints.containsKey(requestURI)) {
			List<String> allowedMethods = allowedEndpoints.get(requestURI);
			return allowedMethods.contains(requestMethod);
		}
		return false;
	}

	private static Map<String, List<String>> getAllowedEndpoints() {
		Map<String, List<String>> allowedEndpoints = new HashMap<>();
		allowedEndpoints.put("/v1/signup", Arrays.asList("POST"));
		allowedEndpoints.put("/v1/login", Arrays.asList("POST"));
		allowedEndpoints.put("/v1/forgot-password", Arrays.asList("PUT"));
		allowedEndpoints.put("/v1/otp/send", Arrays.asList("POST"));
		allowedEndpoints.put("/v1/otp/verify", Arrays.asList("POST"));
		return allowedEndpoints;
	}
}
