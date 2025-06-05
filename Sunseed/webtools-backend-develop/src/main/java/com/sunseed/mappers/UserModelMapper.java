package com.sunseed.mappers;

import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.sunseed.entity.UserProfile;
import com.sunseed.model.responseDTO.UserProfileResponseDto;
import com.sunseed.model.responseDTO.UserResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserModelMapper {

	private final ModelMapper modelMapper;

	/**
	 * Handling mapping between User & UserAuthResponseDto. Explicitly mapped userId
	 * and userProfileId. It resolves issues related to incorrect mapping, such as
	 * attempting to convert non-numeric data like an email address to a numeric
	 * type like Long.
	 */
//	public UserModelMapper(ModelMapper modelMapper) {
//		
//		// Mapping configuration for User entity to UserAuthResponseDto
//	    modelMapper.typeMap(User.class, UserAuthResponseDto.class)
//	               .addMapping(User::getUserId, UserAuthResponseDto::setUserId)
//	               .addMapping(src -> src.getUserProfile().getUserProfileId(), UserAuthResponseDto::setUserProfileId);
//	    
//	    // Mapping configuration for UserProfile entity to UserProfileResponseDto
//	    modelMapper.typeMap(UserProfile.class, UserProfileResponseDto.class)
//	               .addMapping(UserProfile::getUserProfileId, UserProfileResponseDto::setUserProfileId);
////	               .addMapping(UserProfile::getFirstName, UserProfileResponseDto::setFirstName)
////	               .addMapping(UserProfile::getLastName, UserProfileResponseDto::setLastName)
////	               .addMapping(UserProfile::getPhoneNumber, UserProfileResponseDto::setPhoneNumber)
////	               .addMapping(UserProfile::getProfilePicturePath, UserProfileResponseDto::setProfilePicturePath);
//	}

//	public User userRequestDtoToEntity(UserRequestDto userDto) {
//		User user = this.modelMapper.map(userDto, User.class);
//		return user;
//	}

//	public UserResponseDto entityToUserResponseDto(User user) {
//		UserResponseDto userDto = this.modelMapper.map(user, UserResponseDto.class);
//		return userDto;
//	}

//	public UserAuthResponseDto entityToUserAuthResponseDto(User user) {
//		UserAuthResponseDto userDto = this.modelMapper.map(user, UserAuthResponseDto.class);
//		return userDto;
//	}

//	public UserAuthResponseDto entityToUserAuthResponseDto(User user, Long userProfileId) {
//		UserAuthResponseDto userDto = this.modelMapper.map(user, UserAuthResponseDto.class);
//		userDto.setUserProfileId(userProfileId);
//		return userDto;
//	}

	public UserProfileResponseDto entityToUserProfileResponseDto(UserProfile userProfile) {
		UserProfileResponseDto userProfileResponseDto = this.modelMapper.map(userProfile, UserProfileResponseDto.class);
		return userProfileResponseDto;
	}
	
	public UserResponseDto entityToUserResponseDto(UserProfile userProfile) {
		UserResponseDto userResponseDto = new UserResponseDto();
		userResponseDto.setFirstName(userProfile.getFirstName());
		userResponseDto.setLastName(userProfile.getLastName());
		userResponseDto.setPhoneNumber(userProfile.getPhoneNumber());
		return userResponseDto;
	}
	
	public UserResponseDto authServiceToResponseDto(UserProfile userProfile, long userId, boolean isActive,
			boolean isVerified, Set<String> roles, String createdAt, String updatedAt) {
		UserResponseDto responseDto = new UserResponseDto();

		responseDto.setUserProfileId(userProfile.getUserProfileId());
		responseDto.setFirstName(userProfile.getFirstName());
		responseDto.setLastName(userProfile.getLastName());
		responseDto.setPhoneNumber(userProfile.getPhoneNumber());
		responseDto.setEmailId(userProfile.getEmailId());

		responseDto.setId(userId);
		responseDto.setIsActive(isActive);
		responseDto.setIsVerified(isVerified);
		responseDto.setRoles(roles);
		responseDto.setCreatedAt(createdAt);
		responseDto.setUpdatedAt(updatedAt);

		return responseDto;
	}

}
