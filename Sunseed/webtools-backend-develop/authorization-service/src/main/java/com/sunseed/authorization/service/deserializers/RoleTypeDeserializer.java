package com.sunseed.authorization.service.deserializers;

import com.sunseed.authorization.service.enums.RoleType;

public class RoleTypeDeserializer extends EnumDeserializer<RoleType>{

	private static final long serialVersionUID = 1L;

	public RoleTypeDeserializer() {
		super(RoleType.class);
	}
}
