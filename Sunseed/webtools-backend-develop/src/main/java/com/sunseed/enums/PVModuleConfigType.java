package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.sunseed.exceptions.InvalidDataException;

public enum PVModuleConfigType {
	P("P"), L("L"), P_("P'"), L_("L'");

	private final String value;

	PVModuleConfigType(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value.toUpperCase();
	}
	
	@Override
    public String toString() {
        return value;  // Ensures correct representation when printed or serialized
    }

	public static PVModuleConfigType fromString(String text) {
		for (PVModuleConfigType type : PVModuleConfigType.values()) {
			if (type.value.equalsIgnoreCase(text)) {
				return type;
			}
		}

		throw new InvalidDataException("empty.pvModuleConfigType");
	}
}
