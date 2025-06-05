package com.sunseed.deserializers;

import com.sunseed.enums.OtpType;

public class OtpTypeDeserializer extends EnumDeserializer<OtpType> {

	private static final long serialVersionUID = 1L;

	public OtpTypeDeserializer() {
		super(OtpType.class);
	}
}
