package com.sunseed.authorization.service.model.requestDTO;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
public class AdminSignupRequestDto {
	
	@NotBlank(message = "Email ID should not be empty")
	@Email(message = "Invalid email format")
	private String emailId;

	@NotBlank(message = "Password should not be empty")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,16}$", message = "Password must be 8-12 characters long with 1 uppercase & 1 lowercase character , a number and 1 special characters atleast")
	private String password;
	
	@NotNull(message = "Roles should not be empty")
	private Set<String> roles;

}
