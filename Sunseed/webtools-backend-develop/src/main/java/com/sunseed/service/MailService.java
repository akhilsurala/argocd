package com.sunseed.service;

import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;

public interface MailService {

	UserAuthResponseDto sendOTP(String email, String otpFor);

	LoginResponseDto verifyEmailUsingOtp(Integer otp, String email, String otpFor);

}
