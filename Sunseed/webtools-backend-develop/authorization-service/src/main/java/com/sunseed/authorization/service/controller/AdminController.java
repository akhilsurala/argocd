package com.sunseed.authorization.service.controller;

import java.util.List;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.authorization.service.model.requestDTO.BlockUserRequestDto;
import com.sunseed.authorization.service.model.requestDTO.UserRequestDto;
import com.sunseed.authorization.service.model.responseDTO.UserResponseDto;
import com.sunseed.authorization.service.response.ApiResponse;
import com.sunseed.authorization.service.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminController {

	private final UserService userService;

	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(@RequestParam(value = "search", required = false) String search) {

		String decodedSearch = search != null ? URLDecoder.decode(search, StandardCharsets.UTF_8) : null;
		System.out.println(decodedSearch);
		List<UserResponseDto> users = userService.getAllUsers(decodedSearch.toString());
		return ApiResponse.responseHandler(users, "Users fetched successfully", HttpStatus.OK);

	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<Object> getUser(@PathVariable Long userId) {
		UserResponseDto user = userService.getUser(userId);
		return ApiResponse.responseHandler(user, "User fetched successfully", HttpStatus.OK);
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<Object> updateUser(@PathVariable Long userId,
			@Valid @RequestBody UserRequestDto userRequestDto) {

		UserResponseDto userResponseDto = this.userService.updateUser(userId, userRequestDto);

		return ApiResponse.responseHandler(userResponseDto, "User updated successfully", HttpStatus.OK);

	}

	@DeleteMapping("/users/{usersId}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long usersId) {

		UserResponseDto userResponseDto = this.userService.deleteUser(usersId);

		return ApiResponse.responseHandler(userResponseDto, "User deleted successfully", HttpStatus.OK);

	}

	@PutMapping("/users/block/{userId}")
	public ResponseEntity<Object> blockUser(@PathVariable Long userId,
			@Valid @RequestBody BlockUserRequestDto userRequestDto) {

		UserResponseDto userResponseDto = this.userService.blockUser(userId, userRequestDto);

		return ApiResponse.responseHandler(userResponseDto, "User updated successfully", HttpStatus.OK);

	}

}
