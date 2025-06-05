package com.sunseed.model.responseDTO;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseDto {

	private Long id;

	private Long userProfileId;

	private String emailId;

	private String firstName;

	private String lastName;

	private Set<String> roles;

	private String phoneNumber;

	private Boolean isVerified;

	private Boolean isActive;
	
	private String createdAt;
	
	private String updatedAt;

}
