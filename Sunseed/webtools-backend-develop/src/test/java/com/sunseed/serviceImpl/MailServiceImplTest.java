//package com.sunseed.serviceImpl;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyMap;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.when;
//
//import java.time.Instant;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Optional;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.context.MessageSource;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.reactive.function.client.WebClientResponseException;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunseed.entity.UserOtp;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.CommonStatus;
//import com.sunseed.enums.OtpType;
//import com.sunseed.exceptions.InvalidDataException;
//import com.sunseed.exceptions.ResourceNotFoundException;
//import com.sunseed.helper.MailHelper;
//import com.sunseed.model.responseDTO.LoginResponseDto;
//import com.sunseed.model.responseDTO.UserAuthResponseDto;
//import com.sunseed.repository.UserOtpRepo;
//import com.sunseed.repository.UserProfileRepository;
//
//import reactor.core.publisher.Mono;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//public class MailServiceImplTest {
//
//	@InjectMocks
//	private MailServiceImpl mailService;
//
//	@Mock
//	private MailHelper mailHelper;
//	@Mock
//	private WebClient.Builder webClientBuilder;
//
//	@Mock
//	private WebClient webClient;
//
//	@Mock
//	private WebClient.RequestBodyUriSpec uriSpec;
//
//	@Mock
//	private WebClient.RequestHeadersSpec headersSpec;
//
//	@Mock
//	private WebClient.RequestBodySpec bodySpec;
//
//	@Mock
//	private WebClient.ResponseSpec responseSpec;
//
//	@Mock
//	private Mono<Object> bodyMono;
//
//	@Mock
//	private Mono<String> stringMono;
//
//	@Mock
//	private UserOtpRepo userOtpRepo;
//
//	@Mock
//	private UserProfileRepository userProfileRepo;
//
//	@Mock
//	private MessageSource messageSource;
//
//	@Test
//	void testSendOTP_Success() {
//		Map<String, String> userMap = new HashMap<>();
//		userMap.put("emailId", "krishna81.ks@gmail.com");
//
//		// Mocking the response from WebClient
//		Map<String, Object> responseBody = new HashMap<>();
//		responseBody.put("userId", 1L);
//		responseBody.put("isVerified", false);
//		responseBody.put("role", "user");
//
//		// Mock WebClient interactions
//		// when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
//		// when(webClientBuilder.build()).thenReturn(webClient);
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(userMap)).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenReturn(responseBody);
//
//		Object bodyObject = webClient.post().uri("/user").bodyValue(userMap).retrieve().bodyToMono(Object.class)
//				.block();
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode value = objectMapper.valueToTree(bodyObject);
//		Long userId = value.get("userId").asLong();
//		System.out.println(userId);
//
//		// Mocking userProfileRepo response
//		UserProfile userProfile = new UserProfile();
//		userProfile.setUserProfileId(1L);
//		userProfile.setEmailId("krishna81.ks@gmail.com");
//		userProfile.setUserId(2L);
//		userProfile.setFirstName("krishna");
//
//		when(MailHelper.otpGeneration()).thenReturn(123456);
//
//		when(userProfileRepo.findByUserId(userId)).thenReturn(java.util.Optional.of(userProfile));
//
//		userProfile = userProfileRepo.findByUserId(userId).get();
//		int otp = MailHelper.otpGeneration();
//		StringBuilder str = new StringBuilder();
//		str.append("hello your otp is :").append(otp);
//		MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//		formData.add("to", userProfile.getEmailId());
//		formData.add("subject", "email verification");
//		formData.add("body", str.toString());
//
//		String expectedResponse = "mail sent successfully";
//		// when(webClientBuilder.baseUrl(anyString())).thenReturn(webClientBuilder);
//		// when(webClientBuilder.build()).thenReturn(webClient);
//		// when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/send")).thenReturn(bodySpec);
//		when(bodySpec.contentType(MediaType.MULTIPART_FORM_DATA)).thenReturn(bodySpec);
//		when(bodySpec.body(any())).thenReturn(headersSpec);
//		// when(bodySpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just(expectedResponse));
//
//		String result = webClient.post().uri("/send").contentType(MediaType.MULTIPART_FORM_DATA)
//				.body(BodyInserters.fromMultipartData(formData)).retrieve().bodyToMono(String.class).block();
//
//		System.out.println(result);
//
//		// Mocking userOtpRepo response
//		when(userOtpRepo.findByUserProfileId(anyLong())).thenReturn(List.of());
//
//		// Mocking messageSource response
//		when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Mocked Message");
//
//		// Call the method under test
//		UserAuthResponseDto userAuthResponseDto = mailService.sendOTP("krishna81.ks@gmail.com", "email verification");
//
//		// Assertions
//		assertNotNull(userAuthResponseDto);
//		assertEquals(1L, userAuthResponseDto.getUserProfileId());
//		assertEquals("krishna81.ks@gmail.com", userAuthResponseDto.getEmailId());
//		// assertFalse(userAuthResponseDto.isVerified());
//		assertEquals("user", userAuthResponseDto.getRoles());
////        // Add more assertions as needed
//	}
//
//	@Test
//	void testSendOTP_ResourceNotFound() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(ResourceNotFoundException.class,
//				() -> mailService.sendOTP("test@example.com", "email verification"));
//
//	}
//
//	@Test
//	void testSendOTP_BadRequest() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(InvalidDataException.class, () -> mailService.sendOTP("", "email verification"));
//	}
//
//	@Test
//	void testSendOTPNull_BadRequest() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(InvalidDataException.class, () -> mailService.sendOTP(null, "email verification"));
//	}
//
//	@Test
//	void testVerifyEmailUsingOtp_ValidOtpAndVerification() {
//		String email = "krishna81.ks@gmail.com";
//		Integer otp = 123456;
//		String otpFor = OtpType.EMAIL_VERIFICATION.getValue();
//
//		UserProfile userProfile = new UserProfile();
//		userProfile.setUserProfileId(2L);
//		userProfile.setEmailId(email);
//		userProfile.setFirstName("Krishna");
//		userProfile.setPhoneNumber("1234567890");
//
//		UserOtp userOtp = new UserOtp();
//		userOtp.setOtp(otp);
//		userOtp.setCreatedAt(Instant.now()); // Within 60 minutes
//		userOtp.setOtpStatus(CommonStatus.ACTIVE);
//		userOtp.setUserProfileId(userProfile.getUserProfileId());
//
//		List<UserOtp> userOtps = new ArrayList<>();
//		userOtps.add(userOtp);
//
//		when(userProfileRepo.findByEmailId(email)).thenReturn(Optional.of(userProfile));
//		when(userOtpRepo.findByUserProfileId(userProfile.getUserProfileId())).thenReturn(userOtps);
//
//		Map<String, String> reqMap = new HashMap<>();
//		reqMap.put("emailId", "krishna81.ks@gmail.com");
//
//		Map<String, Object> userMap = new HashMap<>();
//		userMap.put("emailId", email);
//		userMap.put("isVerified", true);
//		userMap.put("role", "user");
//		userMap.put("userId", 2L);
//
//		Map<String, Object> responseMap = new HashMap<>();
//		responseMap.put("user", userMap);
//		responseMap.put("accessToken", "Bearer euryfhdghndhjfjdndffhfjdjdmdjcfjfhfbfj");
//
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/v1/verify-user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(reqMap)).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenReturn(responseMap);
//
//		Object bodyObj = webClient.post().uri("/v1/verify-user").bodyValue(reqMap).retrieve().bodyToMono(Object.class)
//				.block();
//
//		ObjectMapper objectMapper = new ObjectMapper();
//		JsonNode value = objectMapper.valueToTree(bodyObj);
//		JsonNode accessTokenNode = value.get("accessToken");
//		Boolean isVerified = value.get("user").get("isVerified").asBoolean();
//		Long userId = value.get("user").get("userId").asLong();
//		System.out.println(accessTokenNode);
//
//		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
//		userAuthResponseDto.setUserProfileId(userProfile.getUserProfileId());
//		userAuthResponseDto.setEmailId(email);
//		userAuthResponseDto.setFirstName(userProfile.getFirstName());
//		userAuthResponseDto.setVerified(isVerified);
//
//		LoginResponseDto loginResponse = new LoginResponseDto();
//
//		loginResponse.setAccessToken(accessTokenNode.asText());
//		loginResponse.setUser(userAuthResponseDto);
//
//		LoginResponseDto loginResponseDto = mailService.verifyEmailUsingOtp(otp, email, otpFor);
//
//		assertNotNull(loginResponseDto);
//		assertNotNull(loginResponseDto.getUser());
//		assertEquals(email, loginResponseDto.getUser().getEmailId());
//		assertEquals(2L, loginResponseDto.getUser().getUserProfileId());
//		assertEquals("Krishna", loginResponseDto.getUser().getFirstName());
//		assertTrue(loginResponseDto.getUser().isVerified());
//		assertNotNull(loginResponseDto.getAccessToken());
//		assertNotNull(loginResponseDto.getUser().getRoles());
//	}
//
//	@Test
//	void testVerifyOTP_ResourceNotFound() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/v1/verify-user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.NOT_FOUND.value(), "Not Found", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(ResourceNotFoundException.class,
//				() -> mailService.sendOTP("test@example.com", "email verification"));
//
//	}
//
//	@Test
//	void testVerifyOTP_BadRequest() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/v1/verify-user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(InvalidDataException.class, () -> mailService.sendOTP("", "email verification"));
//	}
//
//	@Test
//	void testVerifyOTPNull_BadRequest() {
//		// Mocking the response from WebClient for 404
//		when(webClient.post()).thenReturn(uriSpec);
//		when(uriSpec.uri("/v1/verify-user")).thenReturn(bodySpec);
//		when(bodySpec.bodyValue(anyMap())).thenReturn(headersSpec);
//		when(headersSpec.retrieve()).thenReturn(responseSpec);
//		when(responseSpec.bodyToMono(Object.class)).thenReturn(bodyMono);
//		when(bodyMono.block()).thenThrow(
//				WebClientResponseException.create(HttpStatus.BAD_REQUEST.value(), "Bad Request", null, null, null));
//
//		// Call the method under test and assert that it throws a
//		// ResourceNotFoundException
//		assertThrows(InvalidDataException.class, () -> mailService.sendOTP(null, "email verification"));
//	}
//
//}
