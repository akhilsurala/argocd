package com.sunseed.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.responseDTO.DashBoardResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.DashboardService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/admin")
@RequiredArgsConstructor
public class DashboardController {

	private final ApiResponse apiResponse;
	private final DashboardService dashboardService;

	@GetMapping("/home")
	public ResponseEntity<Object> getDashboardDetails(HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		DashBoardResponseDto response = this.dashboardService.getDashboardDetails(userId);
		return apiResponse.loginResponseHandler(response, "details.fetched.successfully", HttpStatus.OK);

	}

}
