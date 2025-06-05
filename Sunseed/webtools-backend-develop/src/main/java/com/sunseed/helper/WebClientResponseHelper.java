package com.sunseed.helper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.model.requestDTO.AdminSignUpRequestDto;
import com.sunseed.model.requestDTO.SignupRequestDto;
import com.sunseed.model.responseDTO.LoginResponseDto;
import com.sunseed.model.responseDTO.SignupResponseDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;

public class WebClientResponseHelper {

	private static ObjectMapper objectMapper = new ObjectMapper();
	private static Set<HttpStatus> allowedStatuses = new HashSet<>(
			Arrays.asList(HttpStatus.CREATED, HttpStatus.BAD_REQUEST, HttpStatus.CONFLICT, HttpStatus.FORBIDDEN,
					HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.UNAUTHORIZED, HttpStatus.UNPROCESSABLE_ENTITY));

	public static boolean isAllowed(HttpStatusCode statusCode) {
		return allowedStatuses.contains(statusCode);
	}

	public static JsonNode extractDataFromResponse(Object object) {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode rootNode = objectMapper.valueToTree(object);
		JsonNode dataNode = rootNode.get("data");

		if (dataNode == null)
			throw new ResourceNotFoundException("data.not.found");
		return dataNode;
	}

	public static UserProfile getUserProfileForSignup(SignupRequestDto request, JsonNode data) {

		String emailId = data.get("user").get("emailId").asText();
		Long userId = data.get("user").get("userId").asLong();

		UserProfile userProfile = UserProfile.builder().emailId(emailId.toLowerCase()).userId(userId)
				.firstName(request.getFirstName()).lastName(request.getLastName()).build();

		return userProfile;
	}

	public static UserProfile getUserProfileForAdminSignup(AdminSignUpRequestDto request, JsonNode data) {

		String emailId = data.get("user").get("emailId").asText();
		Long userId = data.get("user").get("userId").asLong();

		UserProfile userProfile = UserProfile.builder().emailId(emailId.toLowerCase()).userId(userId)
				.firstName(request.getFirstName()).lastName(request.getLastName()).build();

		return userProfile;
	}

	public static LoginResponseDto getLoginResponse(UserProfile userProfile, JsonNode data) {

		boolean isVerified = data.get("user").get("isVerified").asBoolean();
		Object rolesObject = data.get("user").get("roles");
		List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
		});
		Set<String> roles = new HashSet<>(rolesList);
		String accessToken = data.get("accessToken").asText();

		UserAuthResponseDto userResponse = UserAuthResponseDto.builder().emailId(userProfile.getEmailId().toLowerCase())
				.firstName(userProfile.getFirstName()).isVerified(isVerified).lastName(userProfile.getLastName())
				.phoneNumber(userProfile.getPhoneNumber()).roles(roles).userProfileId(userProfile.getUserProfileId())
				.userProfilePicturePath(userProfile.getProfilePicturePath())
				.build();

		LoginResponseDto loginResponse = LoginResponseDto.builder().accessToken(accessToken).user(userResponse).build();

		return loginResponse;
	}

	public static SignupResponseDto getSignupResponse(UserProfile userProfile, JsonNode data) {

		boolean isVerified = data.get("user").get("isVerified").asBoolean();
		Object rolesObject = data.get("user").get("roles");
		List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
		});
		Set<String> roles = new HashSet<>(rolesList);

		UserAuthResponseDto userResponse = UserAuthResponseDto.builder().emailId(userProfile.getEmailId().toLowerCase())
				.firstName(userProfile.getFirstName()).isVerified(isVerified).lastName(userProfile.getLastName())
				.phoneNumber(userProfile.getPhoneNumber()).roles(roles).userProfileId(userProfile.getUserProfileId())
				.build();

		SignupResponseDto signupResponse = SignupResponseDto.builder().user(userResponse).build();
		return signupResponse;
	}

	public static UserAuthResponseDto getUserResponse(UserProfile userProfile, JsonNode data) {

		boolean isVerified = data.get("isVerified").asBoolean();
		Object rolesObject = data.get("roles");
		List<String> rolesList = objectMapper.convertValue(rolesObject, new TypeReference<List<String>>() {
		});
		Set<String> roles = new HashSet<>(rolesList);

		UserAuthResponseDto userResponse = UserAuthResponseDto.builder().emailId(userProfile.getEmailId().toLowerCase())
				.firstName(userProfile.getFirstName()).isVerified(isVerified).lastName(userProfile.getLastName())
				.phoneNumber(userProfile.getPhoneNumber()).roles(roles).userProfileId(userProfile.getUserProfileId())
				.build();
		return userResponse;
	}
}
