package com.sunseed.authorization.service.service;

import com.sunseed.authorization.service.model.requestDTO.AdminSignupRequestDto;
import com.sunseed.authorization.service.model.requestDTO.LoginRequestDto;
import com.sunseed.authorization.service.model.requestDTO.SignupRequestDto;
import com.sunseed.authorization.service.model.responseDTO.AuthorizationResponseDto;
import com.sunseed.authorization.service.model.responseDTO.LoginResponseDto;
import com.sunseed.authorization.service.model.responseDTO.SignupResponseDto;
import com.sunseed.authorization.service.model.responseDTO.UserAuthResponseDto;


public interface AuthenticationService {

	SignupResponseDto signup(SignupRequestDto request);
	LoginResponseDto login(LoginRequestDto request);
	AuthorizationResponseDto authorize(String coreRequestURI);
	UserAuthResponseDto getUser(String emailId);
	LoginResponseDto verifyUser(String emailId);
	UserAuthResponseDto resetPassword(String emailId, String password);
	SignupResponseDto adminSignup(AdminSignupRequestDto request);
}
