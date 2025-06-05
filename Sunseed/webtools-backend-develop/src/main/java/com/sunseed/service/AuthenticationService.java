package com.sunseed.service;

import com.sunseed.model.requestDTO.AdminSignUpRequestDto;
import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.requestDTO.LoginRequestDto;
import com.sunseed.model.requestDTO.SignupRequestDto;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.SignupResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;

public interface AuthenticationService {

	SignupResponseDto signup(SignupRequestDto request);

	LoginResponseDto login(LoginRequestDto request);

	UserAuthResponseDto resetPassword(ForgetPasswordRequestDto request);
	
	SignupResponseDto adminSignup(AdminSignUpRequestDto requestDto, String jwtToken);
}
