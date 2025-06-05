package com.sunseed.model.responseDTO;

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
public class UserProfileResponseDto {

//	private Long userId;
	private Long userProfileId;

	private String firstName;

	private String lastName;

	private String phoneNumber;

	private String profilePicturePath;

}
