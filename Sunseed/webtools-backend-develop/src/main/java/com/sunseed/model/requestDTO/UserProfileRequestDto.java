package com.sunseed.model.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileRequestDto {

	@NotBlank(message = "first name should not be empty")
	private String firstName;

	private String lastName;

	@NotBlank(message = "email id should not be null or empty")
	@Email(message = "Invalid email format")
	private String emailId;

	private String phoneNumber;

//	private List<UserProject> userProjects;

	// profile pic url
	private String profilePicturePath;

}
