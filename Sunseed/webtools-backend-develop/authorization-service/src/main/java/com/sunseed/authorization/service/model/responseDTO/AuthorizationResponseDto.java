package com.sunseed.authorization.service.model.responseDTO;

import java.util.Set;

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
public class AuthorizationResponseDto {

	private Long userId;
	private String emailId;
	private Set<RoleType> roles;
}
