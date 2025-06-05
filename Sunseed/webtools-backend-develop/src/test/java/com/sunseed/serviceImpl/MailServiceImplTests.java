//package com.sunseed.serviceImpl;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import com.sunseed.entity.UserOtp;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.CommonStatus;
//import com.sunseed.exceptions.MailException;
//import com.sunseed.helper.WebClientHelper;
//import com.sunseed.model.responseDTO.UserAuthResponseDto;
//import com.sunseed.repository.UserOtpRepo;
//import com.sunseed.repository.UserProfileRepository;
//
//@ExtendWith(MockitoExtension.class)
//public class MailServiceImplTests {
//
//	@Mock
//	private MessageSource messageSource;
//	@Mock
//	private UserOtpRepo userOtpRepo;
//	@Mock
//	private UserProfileRepository userProfileRepository;
//	@Mock
//	private WebClientHelper webClientHelper;
//
//	@InjectMocks
//	private MailServiceImpl mailServiceImpl;
//
//	/*
//	 * Test cases for sendOTP() method
//	 */
//
//	@DisplayName("Test case for sendOtp() method when userId is null")
//	@Test
//	public void sendOtp_whenUserIdIsNull() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("userId", null);
//		data.put("isVerified", false);
//		data.put("role", "user");
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", data);
//		authResponse.put("message", "User fetched successfully");
//		authResponse.put("httpStatus", HttpStatus.OK);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.OK));
//
//		// then
//		assertThrows(MailException.class, () -> {
//			mailServiceImpl.sendOTP(email, otpFor);
//		});
//	}
//
//	@DisplayName("Test case for sendOtp() method when auth response is bad request")
//	@Test
//	public void sendOtp_whenAuthResponseIsBadRequest() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", null);
//		authResponse.put("message", "Bad request");
//		authResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.BAD_REQUEST));
//
//		// then
//		assertThrows(MailException.class, () -> {
//			mailServiceImpl.sendOTP(email, otpFor);
//		});
//	}
//
//	@DisplayName("Test case for sendOtp() method when auth response is not found")
//	@Test
//	public void sendOtp_whenAuthResponseIsNotFound() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", null);
//		authResponse.put("message", "Not found");
//		authResponse.put("httpStatus", HttpStatus.NOT_FOUND);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.NOT_FOUND));
//
//		// then
//		assertThrows(MailException.class, () -> {
//			mailServiceImpl.sendOTP(email, otpFor);
//		});
//	}
//
//	@DisplayName("Test case for sendOtp() method when auth response is Internal server error")
//	@Test
//	public void sendOtp_whenAuthResponseIsInternalServerError() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", null);
//		authResponse.put("message", "Internal server error");
//		authResponse.put("httpStatus", HttpStatus.INTERNAL_SERVER_ERROR);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		given(webClientHelper.getUser(userMap))
//				.willReturn(new ResponseEntity<>(authResponse, HttpStatus.INTERNAL_SERVER_ERROR));
//
//		// then
//		assertThrows(MailException.class, () -> {
//			mailServiceImpl.sendOTP(email, otpFor);
//		});
//	}
//
//	@DisplayName("Test case for sendOtp() method when user profile does not exists")
//	@Test
//	public void sendOtp_whenUserProfileDoesNotExists() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("userId", 1L);
//		data.put("isVerified", false);
//		data.put("role", "user");
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", data);
//		authResponse.put("message", "User fetched successfully");
//		authResponse.put("httpStatus", HttpStatus.OK);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.OK));
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.empty());
//
//		// then
//		assertThrows(MailException.class, () -> {
//			mailServiceImpl.sendOTP(email, otpFor);
//		});
//	}
//
//	@DisplayName("Test case for sendOtp() method when user doesn't have previous otp's")
//	@Test
//	public void sendOtp_whenUserHasNoPreviousOtp() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("userId", 1L);
//		data.put("isVerified", false);
//		data.put("role", "user");
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", data);
//		authResponse.put("message", "User fetched successfully");
//		authResponse.put("httpStatus", HttpStatus.OK);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		UserProfile userProfile = UserProfile.builder().emailId("test@gmail.com").firstName("test").userId(1L)
//				.userProfileId(1L).build();
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.OK));
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(userOtpRepo.findByUserProfileId(eq(1L))).willReturn(Collections.emptyList());
//		given(messageSource.getMessage(eq("mail.message1"), any(), any()))
//				.willReturn("Hello, <br><br> We have generated a one-time password (OTP) at your request.<h4> OTP is:");
//		given(messageSource.getMessage(eq("mail.message2"), any(), any())).willReturn(
//				"Please get in touch with us if you believe you received this message by mistake.<br><br>Thanks,<br>GIZ Team");
//		given(messageSource.getMessage(eq("mail.subject1"), any(), any())).willReturn("otp for email verification");
//		given(webClientHelper.sendMail(any())).willReturn("otp sent successfully");
//
//		// then
//		UserAuthResponseDto response = mailServiceImpl.sendOTP(email, otpFor);
//		assertThat(response).isNotNull();
//		assertThat(response.getUserProfileId()).isEqualTo(1L);
//		assertThat(response.getEmailId()).isEqualTo("test@gmail.com");
//		assertThat(response.getFirstName()).isEqualTo("test");
//		assertNull(response.getLastName());
//		assertNull(response.getPhoneNumber());
//		assertThat(response.getRoles()).isEqualTo("user");
//	}
//
//	@DisplayName("Test case for sendOtp() method when user have previous otp's")
//	@Test
//	public void sendOtp_whenUserHasPreviousOtp() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "email verification";
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("userId", 1L);
//		data.put("isVerified", false);
//		data.put("role", "user");
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", data);
//		authResponse.put("message", "User fetched successfully");
//		authResponse.put("httpStatus", HttpStatus.OK);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		UserProfile userProfile = UserProfile.builder().emailId("test@gmail.com").firstName("test").userId(1L)
//				.userProfileId(1L).build();
//
//		UserOtp userOtp = UserOtp.builder().otpId(1L).otp(123456).otpStatus(CommonStatus.ACTIVE).userProfileId(1L)
//				.build();
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.OK));
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(userOtpRepo.findByUserProfileId(eq(1L))).willReturn(List.of(userOtp));
//		given(messageSource.getMessage(eq("mail.message1"), any(), any()))
//				.willReturn("Hello, <br><br> We have generated a one-time password (OTP) at your request.<h4> OTP is:");
//		given(messageSource.getMessage(eq("mail.message2"), any(), any())).willReturn(
//				"Please get in touch with us if you believe you received this message by mistake.<br><br>Thanks,<br>GIZ Team");
//		given(messageSource.getMessage(eq("mail.subject1"), any(), any())).willReturn("otp for email verification");
//		given(webClientHelper.sendMail(any())).willReturn("otp sent successfully");
//
//		// then
//		UserAuthResponseDto response = mailServiceImpl.sendOTP(email, otpFor);
//		assertThat(response).isNotNull();
//		assertThat(response.getUserProfileId()).isEqualTo(1L);
//		assertThat(response.getEmailId()).isEqualTo("test@gmail.com");
//		assertThat(response.getFirstName()).isEqualTo("test");
//		assertNull(response.getLastName());
//		assertNull(response.getPhoneNumber());
//		assertThat(response.getRoles()).isEqualTo("user");
//	}
//
//	@DisplayName("Test case for sendOtp() method for forgot password")
//	@Test
//	public void sendOtp_forForgotPassword() {
//
//		// given
//		String email = "test@gmail.com";
//		String otpFor = "forget password";
//
//		Map<String, Object> data = new HashMap<>();
//		data.put("userId", 1L);
//		data.put("isVerified", false);
//		data.put("role", "user");
//
//		Map<String, Object> authResponse = new HashMap<>();
//		authResponse.put("data", data);
//		authResponse.put("message", "User fetched successfully");
//		authResponse.put("httpStatus", HttpStatus.OK);
//
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//
//		UserProfile userProfile = UserProfile.builder().emailId("test@gmail.com").firstName("test").userId(1L)
//				.userProfileId(1L).build();
//
//		UserOtp userOtp = UserOtp.builder().otpId(1L).otp(123456).otpStatus(CommonStatus.ACTIVE).userProfileId(1L)
//				.build();
//
//		given(webClientHelper.getUser(userMap)).willReturn(new ResponseEntity<>(authResponse, HttpStatus.OK));
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(userOtpRepo.findByUserProfileId(eq(1L))).willReturn(List.of(userOtp));
//		given(messageSource.getMessage(eq("mail.message1"), any(), any()))
//				.willReturn("Hello, <br><br> We have generated a one-time password (OTP) at your request.<h4> OTP is:");
//		given(messageSource.getMessage(eq("mail.message2"), any(), any())).willReturn(
//				"Please get in touch with us if you believe you received this message by mistake.<br><br>Thanks,<br>GIZ Team");
//		given(messageSource.getMessage(eq("mail.subject2"), any(), any())).willReturn("otp for forgot password");
//		given(webClientHelper.sendMail(any())).willReturn("otp sent successfully");
//
//		// then
//		UserAuthResponseDto response = mailServiceImpl.sendOTP(email, otpFor);
//		assertThat(response).isNotNull();
//		assertThat(response.getUserProfileId()).isEqualTo(1L);
//		assertThat(response.getEmailId()).isEqualTo("test@gmail.com");
//		assertThat(response.getFirstName()).isEqualTo("test");
//		assertNull(response.getLastName());
//		assertNull(response.getPhoneNumber());
//		assertThat(response.getRoles()).isEqualTo("user");
//	}
//
//
//	/*
//	 *     Test cases for verifyEmailUsingOtp() method
//	 * */
//
//
//}
