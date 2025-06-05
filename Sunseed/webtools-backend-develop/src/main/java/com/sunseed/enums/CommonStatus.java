package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sunseed.deserializers.CommonStatusDeserializer;

@JsonDeserialize(using = CommonStatusDeserializer.class)
public enum CommonStatus {
	ACTIVE("active"), INACTIVE("inactive");

	private String value;

	private CommonStatus(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

}
