package com.sunseed.authorization.service.service;

import java.util.List;

import com.sunseed.authorization.service.model.requestDTO.BlockUserRequestDto;
import com.sunseed.authorization.service.model.requestDTO.UserRequestDto;
import com.sunseed.authorization.service.model.responseDTO.UserResponseDto;

import jakarta.validation.Valid;

public interface UserService {

	List<UserResponseDto> getAllUsers(String search);

	UserResponseDto updateUser(Long userId, UserRequestDto requestDto);

	UserResponseDto deleteUser(Long targetUserId);

	UserResponseDto blockUser(Long userId, @Valid BlockUserRequestDto userRequestDto);

	UserResponseDto getUser(Long userId);

}