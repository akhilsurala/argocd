package com.sunseed.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sunseed.deserializers.CalculationApproachDeserializer;

@JsonDeserialize(using = CalculationApproachDeserializer.class)
public enum CalculationApproach {

	FIXED_LAND("fixed land"), FIXED_PV_CAPACITY("fixed pv capacity");

	private String value;

	private CalculationApproach(String value) {
		this.value = value;
	}

	@JsonValue
	public String getValue() {
		return value;
	}

}
