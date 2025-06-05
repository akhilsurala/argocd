package com.sunseed.model.requestDTO;

import java.util.Set;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class UserRequestDto {

	@NotBlank(message = "empty.email")
	@Email(message = "invalid.email")
	private String emailId;

	@NotEmpty(message = "empty.roles")
	private Set<String> roles;

	@NotBlank(message = "empty.firstName")
	@Size(max = 20, message = "firstname.too.long")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "invalid.format.firstName")
	private String firstName;

	@Size(max = 20, message = "lastname.too.long")
	private String lastName;

	private String phoneNumber;

}
