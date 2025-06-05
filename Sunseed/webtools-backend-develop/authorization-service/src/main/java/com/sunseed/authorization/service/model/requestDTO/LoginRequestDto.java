package com.sunseed.authorization.service.model.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
public class LoginRequestDto {

	@NotBlank(message = "Email ID should not be empty")
	@Email(message = "Invalid email format")
	private String emailId;

	@NotBlank(message = "Password should not be empty")
	private String password;
	
	@Pattern(regexp = "(?i)^(admin|user)$", message = "Sign in as 'user' or 'admin' only")
	private String signInAs;
}
