package com.sunseed.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.requestDTO.LoginRequestDto;
import com.sunseed.model.requestDTO.SignupRequestDto;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.SignupResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.AuthenticationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	private final ApiResponse apiResponse;

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@Valid @RequestBody SignupRequestDto request) {
		
		SignupResponseDto signupResponse = authenticationService.signup(request);
		return apiResponse.commonResponseHandler(signupResponse, "user.created", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto request) {

		LoginResponseDto loginResponse = authenticationService.login(request);
		String message = loginResponse.getAccessToken() != null ? "user.logged" : "email.unverified";
		return apiResponse.commonResponseHandler(loginResponse, message, HttpStatus.OK);
	}

	@PutMapping("/forgot-password")
	public ResponseEntity<Object> resetPassword(@Valid @RequestBody ForgetPasswordRequestDto request) {
		UserAuthResponseDto updatedUser = authenticationService.resetPassword(request);

		return apiResponse.commonResponseHandler(updatedUser, "password.updated", HttpStatus.OK);
	}
}
