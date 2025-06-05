package com.sunseed.service;

import java.util.List;

import com.sunseed.model.requestDTO.BlockUserRequestDto;
import com.sunseed.model.requestDTO.UserRequestDto;
import com.sunseed.model.responseDTO.UserResponseDto;

import jakarta.validation.Valid;

public interface UserService {

	List<UserResponseDto> getAllUsers(String jwtToken, String search);

	UserResponseDto updateUser(Long targetUserId, UserRequestDto requestDto, String jwtToken);

	UserResponseDto deleteUser(Long targetUserId, String jwtToken);

	UserResponseDto blockUser(Long userId, @Valid BlockUserRequestDto blockUserRequestDto, String jwtToken);

	UserResponseDto getUser(Long userId, String jwtToken);
}
