package com.sunseed.controller.mail;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.enums.OtpType;
import com.sunseed.model.requestDTO.EmailVerificationRequestDto;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/otp")
@RequiredArgsConstructor
public class MailController {

	private final ApiResponse apiResponse;

	private final MailService emailService;

	@PostMapping("/send")
	public ResponseEntity<Object> sendOtp(@RequestBody EmailVerificationRequestDto emailRequest) {

		if (emailRequest.getEmail() == null || emailRequest.getOtpFor() == null || emailRequest.getOtpFor().isEmpty()
				|| emailRequest.getEmail().isEmpty()) {
			return apiResponse.errorHandler(HttpStatus.BAD_REQUEST, "field.required");

		}
		if (!(OtpType.EMAIL_VERIFICATION.getValue().equalsIgnoreCase(emailRequest.getOtpFor())
				|| OtpType.FORGET_PASSWORD.getValue().equalsIgnoreCase(emailRequest.getOtpFor()) || OtpType.CHANGE_PASSWORD.getValue().equalsIgnoreCase(emailRequest.getOtpFor()))) {
			return apiResponse.errorHandler(HttpStatus.BAD_REQUEST, "error.invalidotpfor");
		}


		UserAuthResponseDto user = emailService.sendOTP(emailRequest.getEmail().toLowerCase(), emailRequest.getOtpFor());
		return apiResponse.ResponseHandler(true, "sendotp.response", HttpStatus.OK, user);

	}

	// verifies user otp
	// forget @RequestParam("profilePic") MultipartFile image)Password -> return
	// user
	// verify email -> generate & return jwt token
	@PostMapping("/verify")
	public ResponseEntity<Object> validateEmail(@RequestBody EmailVerificationRequestDto emailRequest) {
		if (emailRequest.getEmail() == null || emailRequest.getOtpFor() == null || emailRequest.getOtpFor().isEmpty()
				|| emailRequest.getEmail().isEmpty() || emailRequest.getOtp() == null) {
			return apiResponse.errorHandler(HttpStatus.BAD_REQUEST, "field.required");

		}
		if (!(OtpType.EMAIL_VERIFICATION.getValue().equalsIgnoreCase(emailRequest.getOtpFor())
				|| OtpType.FORGET_PASSWORD.getValue().equalsIgnoreCase(emailRequest.getOtpFor()))) {
			return apiResponse.errorHandler(HttpStatus.BAD_REQUEST, "error.invalidotpfor");
		}
		int length = String.valueOf(emailRequest.getOtp()).length();
		if (length < 6 || length > 6) {
			return apiResponse.errorHandler(HttpStatus.BAD_REQUEST, "error.invalidotp");
		}

		LoginResponseDto res = emailService.verifyEmailUsingOtp(emailRequest.getOtp(), emailRequest.getEmail(),
				emailRequest.getOtpFor());
		return apiResponse.ResponseHandler(true, "otp.verify", HttpStatus.OK, res);
	}

}