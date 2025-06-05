package com.sunseed.model.requestDTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class SignupRequestDto {

	@NotBlank(message = "empty.email")
	@Email(message = "invalid.email")
	private String emailId;

	@NotBlank(message = "empty.password")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^\\da-zA-Z]).{8,16}$", message = "invalid.format.password")
	private String password;

	@NotBlank(message = "empty.firstName")
	@Size(max = 20, message = "firstname.too.long")
	@Pattern(regexp = "^[a-zA-Z]+$", message = "invalid.format.firstName")
	private String firstName;

	@Size(max = 20, message = "lastname.too.long")
	private String lastName;
}
