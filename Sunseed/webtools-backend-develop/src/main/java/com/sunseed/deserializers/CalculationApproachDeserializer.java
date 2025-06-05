package com.sunseed.deserializers;

import com.sunseed.enums.CalculationApproach;

public class CalculationApproachDeserializer extends EnumDeserializer<CalculationApproach> {

	private static final long serialVersionUID = 1L;

	public CalculationApproachDeserializer() {
		super(CalculationApproach.class);
	}
}
