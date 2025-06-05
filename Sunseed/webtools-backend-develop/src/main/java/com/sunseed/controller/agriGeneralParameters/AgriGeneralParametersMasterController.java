package com.sunseed.controller.agriGeneralParameters;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.response.ApiResponse;
import com.sunseed.service.AgriGeneralParametersService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class AgriGeneralParametersMasterController {

	private final ApiResponse apiResponse;
	private final AgriGeneralParametersService agriGeneralParametersService;

	@GetMapping("/agriGeneralParameters/master")
	public ResponseEntity<Object> getMasterData() {
		Map<String, Object> response = agriGeneralParametersService.getMasterData();
		return apiResponse.ResponseHandler(true, "master.response", HttpStatus.OK, response);
	}
}
