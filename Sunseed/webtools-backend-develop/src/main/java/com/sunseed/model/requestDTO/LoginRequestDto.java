package com.sunseed.model.requestDTO;

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

	@NotBlank(message = "empty.email")
	@Email(message = "invalid.email")
	private String emailId;

	@NotBlank(message = "empty.password")
	private String password;
	
	@NotBlank(message = "empty.signInAs")
	@Pattern(regexp = "(?i)^(admin|user)$", message = "invalid.roles")
	private String signInAs;
}
