package com.sunseed.enums;

public enum TempControl {
	TRAIL_MIN_MAX("Trail Min Max"), ABSOLUTE_MIN_MAX("Absolute Min Max"), NONE("none");

	private final String tempControlType;

	private TempControl(final String type) {
		this.tempControlType = type;
	}

	public String getValue() {
		return tempControlType;
	}

}
