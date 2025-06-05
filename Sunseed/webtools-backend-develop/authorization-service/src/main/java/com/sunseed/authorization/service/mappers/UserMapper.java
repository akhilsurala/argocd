package com.sunseed.authorization.service.mappers;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.sunseed.authorization.service.entity.User;
import com.sunseed.authorization.service.model.requestDTO.AdminSignupRequestDto;
import com.sunseed.authorization.service.model.requestDTO.SignupRequestDto;
import com.sunseed.authorization.service.model.responseDTO.AuthorizationResponseDto;
import com.sunseed.authorization.service.model.responseDTO.UserAuthResponseDto;
import com.sunseed.authorization.service.model.responseDTO.UserResponseDto;

@Component
public class UserMapper {

	@Autowired
	private PasswordEncoder passwordEncoder;

	public UserAuthResponseDto userToUserAuthResponseDto(User user) {

		UserAuthResponseDto userAuthResponseDto = new UserAuthResponseDto();
		userAuthResponseDto.setUserId(user.getId());
		userAuthResponseDto.setEmailId( user.getEmailId().toLowerCase());
		userAuthResponseDto.setIsVerified(user.getIsVerified());
		userAuthResponseDto
				.setRoles(user.getRoles().stream().map(role -> role.getRoleType()).collect(Collectors.toSet()));
		return userAuthResponseDto;
	}

	public User registerRequestDtoToUser(SignupRequestDto registerRequestDto) {

		User user = new User();
		user.setEmailId(registerRequestDto.getEmailId().toLowerCase());
		user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
		return user;
	}
	
	public User registerRequestDtoToUserAdmin(AdminSignupRequestDto registerRequestDto) {

		User user = new User();
		user.setEmailId(registerRequestDto.getEmailId().toLowerCase());
		user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));
		user.setIsVerified(true);
		return user;
	}

	public AuthorizationResponseDto userToAuthorizationResponseDto(User user) {

		AuthorizationResponseDto authorizationResponseDto = new AuthorizationResponseDto();
		authorizationResponseDto.setEmailId(user.getEmailId());
		authorizationResponseDto.setUserId(user.getId());
		authorizationResponseDto
				.setRoles(user.getRoles().stream().map(role -> role.getRoleType()).collect(Collectors.toSet()));
		return authorizationResponseDto;
	}

	public UserResponseDto userToResponseDto(User user) {
		UserResponseDto userResponseDto = new UserResponseDto();
		userResponseDto.setIsActive(user.getIsActive());
		userResponseDto.setRoles(
				user.getRoles().stream().map(role -> role.getRoleType().getRoleType()).collect(Collectors.toSet()));
		userResponseDto.setUserId(user.getId());
		userResponseDto.setIsVerified(user.getIsVerified());
		userResponseDto.setCreatedAt(user.getCreatedAt());
		userResponseDto.setUpdatedAt(user.getUpdatedAt());
		return userResponseDto;
	}

}