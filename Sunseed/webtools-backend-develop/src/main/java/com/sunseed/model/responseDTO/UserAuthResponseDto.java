package com.sunseed.model.responseDTO;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({ "userProfileId", "emailId", "roles", "isVerified", "firstName", "lastName", "phoneNumber" })
public class UserAuthResponseDto {

	private Long userProfileId;
	private String emailId;
	private Set<String> roles;

	@JsonProperty("isVerified")
	private boolean isVerified;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String userProfilePicturePath;
}
