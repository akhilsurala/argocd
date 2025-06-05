package com.sunseed.simtool.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggerUtil {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static <T> void toJson(T object) {
		try {
			String json = objectMapper.writeValueAsString(object);
			log.info("NodeRequest Body: {}", json);
		} catch (JsonProcessingException e) {
			log.debug("Failed to serialize object: " + e.getMessage());
		}
	}
}
