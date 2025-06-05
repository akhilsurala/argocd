package com.sunseed.deserializers;

import com.sunseed.enums.RunStatus;

public class RunStatusDeserializer extends EnumDeserializer<RunStatus> {

	private static final long serialVersionUID = 1L;

	public RunStatusDeserializer() {
		super(RunStatus.class);
	}
}
