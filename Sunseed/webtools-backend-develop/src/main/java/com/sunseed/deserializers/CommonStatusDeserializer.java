package com.sunseed.deserializers;

import com.sunseed.enums.CommonStatus;

public class CommonStatusDeserializer extends EnumDeserializer<CommonStatus> {

	private static final long serialVersionUID = 1L;

	public CommonStatusDeserializer() {
		super(CommonStatus.class);
	}
}
