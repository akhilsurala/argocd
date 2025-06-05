package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PreProcessorStatus {
	
	DRAFT("draft"), CREATED("created");

	private String value;

	private PreProcessorStatus(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

}
