package com.sunseed.authorization.service.model.responseDTO;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sunseed.authorization.service.enums.RoleType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "userId", "emailId", "isVerified", "role" })
public class UserAuthResponseDto {

	private Long userId;
	private String emailId;
	private Boolean isVerified;
	private Set<RoleType> roles;
}
