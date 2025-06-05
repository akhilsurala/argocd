package com.sunseed.service;

import com.sunseed.model.requestDTO.ForgetPasswordRequestDto;
import com.sunseed.model.responseDTO.UserAuthResponseDto;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.model.requestDTO.UserProfileRequestDto;
import com.sunseed.model.responseDTO.UserProfileResponseDto;

public interface UserProfileService {

	UserProfileResponseDto updateUserProfile(UserProfileRequestDto userProfileRequestDto, long userId,
			MultipartFile image);
	UserProfileResponseDto getUserDetailsResponse(Long userId);

	UserAuthResponseDto changePassword(ForgetPasswordRequestDto request, Long userId);

}
