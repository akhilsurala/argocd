//package com.sunseed.controller;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyInt;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.mockito.junit.jupiter.MockitoSettings;
//import org.mockito.quality.Strictness;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.node.NullNode;
//import com.sunseed.controller.mail.MailController;
//import com.sunseed.model.requestDTO.EmailVerificationRequestDto;
//import com.sunseed.model.responseDTO.LoginResponseDto;
//import com.sunseed.model.responseDTO.UserAuthResponseDto;
//import com.sunseed.response.ApiResponse;
//import com.sunseed.service.MailService;
//
//@ExtendWith(MockitoExtension.class)
//@MockitoSettings(strictness = Strictness.LENIENT)
//class MailControllerTest {
//
//	private MockMvc mockMvc;
//
//	@Mock
//	private ApiResponse apiResponse;
//
//	@Mock
//	private MailService mailService;
//
//	@InjectMocks
//	private MailController mailController;
//
//	private ObjectMapper objectMapper;
//
//	@BeforeEach
//	public void setUp() {
//		mockMvc = MockMvcBuilders.standaloneSetup(mailController).build();
//		objectMapper = new ObjectMapper();
//	}
//
//	@Test
//	void testSendOtp_Success() throws Exception {
//		// mockMvc = MockMvcBuilders.standaloneSetup(mailController).build();
//
//		// Create a JSONObject for the request body
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna@gmail.com");
//		emailRequest.setOtpFor("email verification");
//		// Convert the JSONObject to a string
//
//		// Mock email service response
//		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
//		userAuthResponseDto.setUserProfileId(1L);
//		userAuthResponseDto.setEmailId("krishna@example.com");
//		userAuthResponseDto.setFirstName("krishna");
//		userAuthResponseDto.setVerified(false);
//		userAuthResponseDto.setRoles(Set.of("User"));
//
//		// Mock email service
//		when(mailService.sendOTP(anyString(), any())).thenReturn(userAuthResponseDto);
//
//		// Prepare response for controller method
//		Map<String, Object> response = new HashMap<>();
//		response.put("success", true);
//		response.put("message", "otp sent successfully");
//		response.put("httpStatus", 200);
//		response.put("data", userAuthResponseDto);
//
////	      invoke api response handler method
//		when(apiResponse.ResponseHandler(eq(true), eq("sendotp.response"), eq(HttpStatus.OK), any()))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//
//		MvcResult result = mockMvc.perform(post("/v1/otp/send").contentType(MediaType.APPLICATION_JSON)
//				.content(objectMapper.writeValueAsBytes(emailRequest))).andExpect(status().isOk()).andReturn();
//
//		String content = result.getResponse().getContentAsString();
//		System.out.println(content);
//
//		JsonNode value = objectMapper.readTree(content);
//		System.out.println(value.get("data").get("userProfileId").asLong());
//		// System.out.println(value.get("data").get("userProfileId").asLong());
//
//		assertEquals("application/json", result.getResponse().getContentType());
//		assertEquals(200, result.getResponse().getStatus());
//		String expectedResponse = objectMapper.writeValueAsString(response);
//		assertEquals(expectedResponse, content);
//
//	}
//
//	@Test
//	void testSendOtp_BadRequestEmailNull() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setOtpFor("email verification");
//
//		// Convert the JSONObject to a string
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////	      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////		mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/send").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testSendOtp_BadRequest_OtpFor_Null() throws Exception {
//
//		// Create a JSONObject for the request body
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna@gmail.com");
//
//		// Convert the JSONObject to a string
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/send").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testSendOtp_BadRequest_Email_Empty() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("");
//		emailRequest.setOtpFor("email verification");
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/send").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testSendOtp_BadRequest_OtpFor_Empty() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna@gmail.com");
//		emailRequest.setOtpFor("");
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/send").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_Success() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishnagmail.com");
//		emailRequest.setOtpFor("email verification");
//		emailRequest.setOtp(123456);
//
//		// Mock email service response
//		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
//		userAuthResponseDto.setUserProfileId(1L);
//		userAuthResponseDto.setEmailId("krishna81.ks@gmail.com");
//		userAuthResponseDto.setFirstName("krishna");
//		userAuthResponseDto.setVerified(false);
//		userAuthResponseDto.setRoles(Set.of("user"));
//
//		LoginResponseDto loginResponseDto = new LoginResponseDto();
//		loginResponseDto.setAccessToken("token");
//		loginResponseDto.setUser(userAuthResponseDto);
//
//		// Mock email service
//		when(mailService.verifyEmailUsingOtp(anyInt(), anyString(), any())).thenReturn(loginResponseDto);
//
//		// Prepare response for controller method
//		Map<String, Object> response = new HashMap<>();
//		response.put("success", true);
//		response.put("message", "email verified");
//		response.put("httpStatus", 200);
//		response.put("data", loginResponseDto);
//
////		      invoke api response handler method
//		when(apiResponse.ResponseHandler(eq(true), eq("otp.verify"), eq(HttpStatus.OK), any()))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isOk()).andReturn();
//
//		String content = result.getResponse().getContentAsString();
//		System.out.println(content);
//
//		JsonNode value = objectMapper.readTree(content);
//		System.out.println(value.get("data").get("user").get("userProfileId").asLong());
//		String token = value.get("data").get("accessToken").asText();
//		// System.out.println(value.get("data").get("userProfileId").asLong());
//
////			assertEquals("application/json", result.getResponse().getContentType());
//
//		assertEquals(200, result.getResponse().getStatus());
//		assertEquals("token", token);
//		String expectedResponse = objectMapper.writeValueAsString(response);
//		assertEquals(expectedResponse, content);
//
//	}
//
//	@Test
//	void testValidateEmailForForgetPassword_Success() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna@gmail.com");
//		emailRequest.setOtpFor("forget password");
//		emailRequest.setOtp(123456);
//
//		// Mock email service response
//		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
//		userAuthResponseDto.setUserProfileId(1L);
//		userAuthResponseDto.setEmailId("krishna81.ks@gmail.com");
//		userAuthResponseDto.setFirstName("krishna");
//		userAuthResponseDto.setVerified(false);
//		userAuthResponseDto.setRoles(Set.of("user"));
//
//		LoginResponseDto loginResponseDto = new LoginResponseDto();
//		loginResponseDto.setAccessToken(null);
//		loginResponseDto.setUser(userAuthResponseDto);
//
//		// Mock email service
//		when(mailService.verifyEmailUsingOtp(anyInt(), anyString(), any())).thenReturn(loginResponseDto);
//
//		// Prepare response for controller method
//		Map<String, Object> response = new HashMap<>();
//		response.put("success", true);
//		response.put("message", "email verified");
//		response.put("httpStatus", 200);
//		response.put("data", loginResponseDto);
//
////		      invoke api response handler method
//		when(apiResponse.ResponseHandler(eq(true), eq("otp.verify"), eq(HttpStatus.OK), any()))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isOk()).andReturn();
//
//		String content = result.getResponse().getContentAsString();
//		System.out.println(content);
//
//		JsonNode value = objectMapper.readTree(content);
//		System.out.println(value.get("data").get("user").get("userProfileId").asLong());
//		String token = value.get("data").get("accessToken").asText();
//		System.out.println(token);
//		// System.out.println(value.get("data").get("userProfileId").asLong());
//
////			assertEquals("application/json", result.getResponse().getContentType());
//
//		assertEquals(200, result.getResponse().getStatus());
//		assertTrue(value.get("data").get("accessToken") instanceof NullNode);
//		String expectedResponse = objectMapper.writeValueAsString(response);
//		assertEquals(expectedResponse, content);
//
//	}
//
//	@Test
//	void testValidateEmail_BadRequest_OtpFor_Empty() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna@gmail.com");
//		emailRequest.setOtpFor("");
//		emailRequest.setOtp(123456);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_BadRequest_OtpFor_Null() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna81.ks@gmail.com");
//		emailRequest.setOtpFor(null);
//		emailRequest.setOtp(123456);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_BadRequest_Otp_Null() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna81.ks@gmail.com");
//		emailRequest.setOtpFor("email verification");
//		emailRequest.setOtp(null);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_BadRequest_email_Empty() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("");
//		emailRequest.setOtpFor("");
//		emailRequest.setOtp(123456);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_BadRequest_email_Null() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail(null);
//		emailRequest.setOtpFor("email verification");
//		emailRequest.setOtp(123456);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("field.required")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_Invalid_Otp() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna81.ks@gmail.com");
//		emailRequest.setOtpFor("email verification");
//		emailRequest.setOtp(1234);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("error.invalidotp")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	@Test
//	void testValidateEmail_Invalid_Otp_greater() throws Exception {
//
//		EmailVerificationRequestDto emailRequest = new EmailVerificationRequestDto();
//		emailRequest.setEmail("krishna81.ks@gmail.com");
//		emailRequest.setOtpFor("email verification");
//		emailRequest.setOtp(123434532);
//
//		Map<String, Object> response = new HashMap<>();
//		response.put("httpStatus", 400);
//		response.put("message", "fields are required");
//
////		      invoke api response handler method
//		when(apiResponse.errorHandler(eq(HttpStatus.BAD_REQUEST), eq("error.invalidotp")))
//				.thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//		// Perform the request and assert the response
////			mockMvc.perform(MockMvcRequestBuilders.post("/send").contentType(MediaType.APPLICATION_JSON)
//		// .content(asJsonString(emailRequest))).andExpect(MockMvcResultMatchers.status().isBadRequest());
//
//		MvcResult result = mockMvc.perform(
//				post("/v1/otp/verify").contentType(MediaType.APPLICATION_JSON).content(asJsonString(emailRequest)))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		// System.out.println( result.getResponse().getStatus());
//		assertEquals(400, result.getResponse().getStatus());
//
//	}
//
//	// Utility method to convert object to JSON string
//	private String asJsonString(final Object obj) {
//		try {
//			return objectMapper.writeValueAsString(obj);
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//}