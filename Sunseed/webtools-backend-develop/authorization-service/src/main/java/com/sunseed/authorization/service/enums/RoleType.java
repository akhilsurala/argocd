package com.sunseed.authorization.service.enums;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.sunseed.authorization.service.deserializers.RoleTypeDeserializer;
import com.sunseed.authorization.service.exceptions.EnumException;

@JsonDeserialize(using = RoleTypeDeserializer.class)
public enum RoleType {

	USER("user"), ADMIN("admin");

	private String roleType;

	private RoleType(String roleType) {
		this.roleType = roleType;
	}

	@JsonValue
	public String getRoleType() {
		return roleType;
	}

	public static Set<RoleType> getListOfRoleTypes(Set<String> roles) {

		if (roles == null || roles.isEmpty()) {
			throw new EnumException("Roles cannot be null or empty",HttpStatus.UNPROCESSABLE_ENTITY);
		}

		Set<RoleType> roleTypeList = new HashSet<>();

		for (String role : roles) {
			boolean isValidEnum = false;
			for (RoleType roleType : RoleType.values()) {
				if (roleType.getRoleType().equalsIgnoreCase(role)) {
					isValidEnum = true;
					roleTypeList.add(roleType);
					break;
				}
			}
			if (!isValidEnum)
				throw new EnumException("Invalid roles provided",HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return roleTypeList;
	}

}
