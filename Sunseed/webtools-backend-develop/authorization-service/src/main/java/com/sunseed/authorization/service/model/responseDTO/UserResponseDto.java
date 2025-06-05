package com.sunseed.authorization.service.model.responseDTO;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponseDto {

	private Long userId;

	private Boolean isActive;

	private Boolean isVerified;

	private Set<String> roles;
	
	private Instant createdAt;
	
	private Instant updatedAt;

}
