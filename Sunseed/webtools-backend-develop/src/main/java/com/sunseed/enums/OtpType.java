package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sunseed.deserializers.OtpTypeDeserializer;

@JsonDeserialize(using = OtpTypeDeserializer.class)
public enum OtpType {

	// forget password, 1
	FORGET_PASSWORD("forget password"),

	// to verify user email, 2
	EMAIL_VERIFICATION("email verification"),

	// to change password
	CHANGE_PASSWORD("change password");

	private final String type;

	private OtpType(final String type) {
		this.type = type;
	}

	@JsonValue
	public String getValue() {
		return type;
	}
}
