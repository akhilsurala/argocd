package com.sunseed.model.responseDTO;

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
@JsonPropertyOrder({ "accessToken", "user" })
public class LoginResponseDto {

	private String accessToken;
	private UserAuthResponseDto user;
}
