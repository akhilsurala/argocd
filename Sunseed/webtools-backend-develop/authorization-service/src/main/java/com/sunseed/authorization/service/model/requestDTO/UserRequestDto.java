package com.sunseed.authorization.service.model.requestDTO;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {

	@NotBlank(message = "Email ID should not be empty")
	@Email(message = "Invalid email format")
	private String emailId;

	@NotEmpty(message = "roles can't be null or empty")
	private Set<String> roles;
	
	@NotNull(message = "isActive must not be null")
	private boolean isActive;

}
