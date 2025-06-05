package com.sunseed.authorization.service.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.authorization.service.model.requestDTO.AdminSignupRequestDto;
import com.sunseed.authorization.service.model.requestDTO.LoginRequestDto;
import com.sunseed.authorization.service.model.requestDTO.ResetPasswordRequestDto;
import com.sunseed.authorization.service.model.requestDTO.SignupRequestDto;
import com.sunseed.authorization.service.model.responseDTO.AuthorizationResponseDto;
import com.sunseed.authorization.service.model.responseDTO.LoginResponseDto;
import com.sunseed.authorization.service.model.responseDTO.SignupResponseDto;
import com.sunseed.authorization.service.model.responseDTO.UserAuthResponseDto;
import com.sunseed.authorization.service.response.ApiResponse;
import com.sunseed.authorization.service.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AuthenticationController {

	private final AuthenticationService authenticationService;

	@PostMapping("/signup")
	public ResponseEntity<Object> signup(@Valid @RequestBody SignupRequestDto request) {

		SignupResponseDto signupResponse = authenticationService.signup(request);
		return ApiResponse.responseHandler(signupResponse, "User Registered Successfully", HttpStatus.CREATED);
	}
	
	@PostMapping("/adminSignup")
	public ResponseEntity<Object> adminSignup(@Valid @RequestBody AdminSignupRequestDto request) {

		System.out.println("Entered here in adminSignup controller");
		SignupResponseDto signupResponse = authenticationService.adminSignup(request);
		return ApiResponse.responseHandler(signupResponse, "User Registered Successfully", HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<Object> login(@Valid @RequestBody LoginRequestDto request) {

		LoginResponseDto loginResponse = authenticationService.login(request);
		String message = loginResponse.getAccessToken() != null ? "User logged in Successfully" : "Email not verified";
		return ApiResponse.responseHandler(loginResponse, message, HttpStatus.OK);
	}

	@GetMapping("/authorize")
	public ResponseEntity<Object> authorize(HttpServletRequest request) {

		System.out.println("inside authorize method");
		String coreRequestURI = request.getHeader("coreRequestURI");
		AuthorizationResponseDto authorizationResponse = authenticationService.authorize(coreRequestURI);
		System.out.println(authorizationResponse);
		return ApiResponse.responseHandler(authorizationResponse, "User is Authorized", HttpStatus.OK);
	}

	@PostMapping("/user")
	public ResponseEntity<Object> getUser(@RequestBody Map<String, String> userMap) {
		
		UserAuthResponseDto userResponse = authenticationService.getUser(userMap.get("emailId"));
		return ApiResponse.responseHandler(userResponse, "User is fetched Successfully", HttpStatus.OK);
		
	}
	
	@PostMapping("/verify-user")
	public ResponseEntity<Object> verifyUser(@RequestBody Map<String, String> userMap) {
		LoginResponseDto userResponse = authenticationService.verifyUser(userMap.get("emailId"));
		return ApiResponse.responseHandler(userResponse, "User is verified Successfully", HttpStatus.OK);
	}
	
	@PutMapping("/reset-password")
	public ResponseEntity<Object> resetPassword(@Valid @RequestBody ResetPasswordRequestDto request) {
		UserAuthResponseDto updatedUser = authenticationService.resetPassword(request.getEmailId(), request.getPassword());
		return ApiResponse.responseHandler(updatedUser, "Password updated successfully", HttpStatus.OK);
	}
}
