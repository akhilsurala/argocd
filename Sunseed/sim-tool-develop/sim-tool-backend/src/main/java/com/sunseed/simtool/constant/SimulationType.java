package com.sunseed.simtool.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SimulationType {
	ONLY_PV("Only PV"), ONLY_AGRI("Only Agri"), APV("APV");
	
	private final String value;
	
	private SimulationType(String value) {
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	@JsonCreator
	public static SimulationType fromString(String value) {
		if(value.equalsIgnoreCase("Only PV"))
			return ONLY_PV;
		else if(value.equalsIgnoreCase("Only Agri"))
			return ONLY_AGRI;
		else if(value.equalsIgnoreCase("APV"))
			return APV;
		return null;
	}
	
	@JsonValue
	public String toJson()
	{
		return value;
	}
}
