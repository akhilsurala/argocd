package com.sunseed.controller.user;

import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.model.requestDTO.UserProfileRequestDto;
import com.sunseed.model.responseDTO.UserProfileResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.UserProfileService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class UserProfileController {

	private final ApiResponse apiResponse;
	private final UserProfileService userProfileService;
	@Value("${project.image}")
	private String path;

	@PutMapping("/profile")
	public ResponseEntity<Object> updateUser(HttpServletRequest request, @RequestParam("firstName") String firstName,
			@RequestParam(value = "lastName",required = false) String lastName, @RequestParam(value = "phoneNumber",required = false) String phoneNumber,
			@RequestParam(value = "profilePic",required = false) MultipartFile image) {

		Long userId = (Long) request.getAttribute("userId");
		UserProfileRequestDto userProfileRequestDto = new UserProfileRequestDto();
		userProfileRequestDto.setFirstName(firstName);
		userProfileRequestDto.setLastName(lastName);
		userProfileRequestDto.setPhoneNumber(phoneNumber);

		UserProfileResponseDto updatedUserProfile = this.userProfileService.updateUserProfile(userProfileRequestDto,
				userId, image);
		return apiResponse.ResponseHandler(true, "userProfile.updated", HttpStatus.OK, updatedUserProfile);

	}
	@GetMapping("/profile")
	public ResponseEntity<Object> getUserDetails(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		UserProfileResponseDto userProfileResponseDto = userProfileService.getUserDetailsResponse(userId);
		return apiResponse.ResponseHandler(true, "userProfile.fetched", HttpStatus.OK, userProfileResponseDto);

	}

	@PutMapping("/change-password")
	public ResponseEntity<Object> changePassword(HttpServletRequest request,@Valid @RequestBody ForgetPasswordRequestDto requestDto) {
		Long userId = (Long) request.getAttribute("userId");
		UserAuthResponseDto userProfileResponseDto = userProfileService.changePassword(requestDto, userId);
		return apiResponse.ResponseHandler(true, "changed.password", HttpStatus.OK, userProfileResponseDto);

	}
}
