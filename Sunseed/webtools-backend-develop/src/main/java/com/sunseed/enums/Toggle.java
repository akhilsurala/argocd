package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Toggle {

	APV("APV"),
	ONLY_PV("Only Pv"),
	ONLY_AGRI("Only Agri");
	
	private String value;
	
	private Toggle(String value) {
		this.value  = value;
	}
	
	@JsonValue
	public String getValue() {
		return value;
	}
	@JsonCreator
	public static Toggle fromValue(String value) {
		for (Toggle toggle : Toggle.values()) {
			if (toggle.getValue().equalsIgnoreCase(value)) {
				return toggle;
			}
		}
		return null;
	}
}
