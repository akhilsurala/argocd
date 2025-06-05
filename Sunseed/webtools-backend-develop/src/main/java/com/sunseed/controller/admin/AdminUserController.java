package com.sunseed.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.AdminSignUpRequestDto;
import com.sunseed.model.requestDTO.BlockUserRequestDto;
import com.sunseed.model.requestDTO.UserRequestDto;
import com.sunseed.model.responseDTO.SignupResponseDto;
import com.sunseed.model.responseDTO.UserResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.AuthenticationService;
import com.sunseed.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class AdminUserController {

	private final ApiResponse apiResponse;
	private final UserService userService;
	private final AuthenticationService authenticationService;

	@GetMapping("/users")
	public ResponseEntity<Object> getUsers(HttpServletRequest request,
			@RequestParam(value = "search", required = false) String search) {

		String jwtToken = (String) request.getAttribute("Authorization");
		List<UserResponseDto> allUsers = this.userService.getAllUsers(jwtToken, search.toString());
		return apiResponse.loginResponseHandler(allUsers, "users.fetched.successfully", HttpStatus.OK);
	}

	@GetMapping("/users/{userId}")
	public ResponseEntity<Object> getUser(@PathVariable Long userId,HttpServletRequest request){
		String jwtToken = (String) request.getAttribute("Authorization");
		UserResponseDto response = this.userService.getUser(userId, jwtToken);
		return apiResponse.commonResponseHandler(response, "user.fetched", HttpStatus.OK);
		
	}

	@PostMapping("/adminSignup")
	public ResponseEntity<Object> signup(@Valid @RequestBody AdminSignUpRequestDto requestDto, HttpServletRequest request) {

		String jwtToken = (String) request.getAttribute("Authorization");
		SignupResponseDto signupResponse = authenticationService.adminSignup(requestDto, jwtToken);
		return apiResponse.commonResponseHandler(signupResponse, "user.created", HttpStatus.CREATED);
	}

	@PutMapping("/users/{userId}")
	public ResponseEntity<Object> updateUser(@PathVariable Long userId,
			@Valid @RequestBody UserRequestDto userRequestDto, HttpServletRequest request) {

		String jwtToken = (String) request.getAttribute("Authorization");
		UserResponseDto userResponseDto = this.userService.updateUser(userId, userRequestDto, jwtToken);

		return apiResponse.loginResponseHandler(userResponseDto, "user.updated.successfully", HttpStatus.OK);

	}

	@PutMapping("/users/block/{userId}")
	public ResponseEntity<Object> blockUser(@PathVariable Long userId,
			@Valid @RequestBody BlockUserRequestDto blockUserRequestDto, HttpServletRequest request) {

		String jwtToken = (String) request.getAttribute("Authorization");
		UserResponseDto userResponseDto = this.userService.blockUser(userId, blockUserRequestDto, jwtToken);

		return apiResponse.loginResponseHandler(userResponseDto, "user.updated.successfully", HttpStatus.OK);

	}

	@DeleteMapping("/users/{usersId}")
	public ResponseEntity<Object> deleteUser(@PathVariable Long usersId, HttpServletRequest request) {

		String jwtToken = (String) request.getAttribute("Authorization");
		UserResponseDto userResponseDto = this.userService.deleteUser(usersId, jwtToken);

		return apiResponse.loginResponseHandler(userResponseDto, "user.deleted.successfully", HttpStatus.OK);

	}

}
