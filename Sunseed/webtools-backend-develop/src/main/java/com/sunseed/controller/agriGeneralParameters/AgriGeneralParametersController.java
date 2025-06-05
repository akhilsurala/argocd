package com.sunseed.controller.agriGeneralParameters;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.AgriGeneralParametersRequestDto;
import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.AgriGeneralParametersService;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class AgriGeneralParametersController {

	private final AgriGeneralParametersService agriGeneralParametersService;
	private final ApiResponse apiResponse;

	@PostMapping("/{projectId}/agriGeneralParameters")
	public ResponseEntity<Object> addAgriGeneralParameters(@RequestBody AgriGeneralParametersRequestDto request,
			@PathVariable Long projectId, @Nullable @RequestParam Long runId, HttpServletRequest httpRequest) {

		System.out.println("Agri general parameters request dto :"+request);
		Long userId = (Long) httpRequest.getAttribute("userId");
		Map<String, Object> serviceResponse = agriGeneralParametersService.addAgriGeneralParameters(request, projectId,
				runId, userId);
		AgriGeneralParametersResponseDto response = (AgriGeneralParametersResponseDto) serviceResponse.get("response");
		String message = (String) serviceResponse.get("message");
		return apiResponse.commonResponseHandler(response, message, HttpStatus.CREATED);
	}

	@PutMapping("/{projectId}/agriGeneralParameters/{agriGeneralParameterId}")
	public ResponseEntity<Object> updateAgriGeneralParameters(@RequestBody AgriGeneralParametersRequestDto request,
			@PathVariable Long projectId, @PathVariable Long agriGeneralParameterId, @Nullable @RequestParam Long runId,
			HttpServletRequest httpRequest) {

		Long userId = (Long) httpRequest.getAttribute("userId");
		Map<String, Object> serviceResponse = agriGeneralParametersService.updateAgriGeneralParameters(request,
				projectId, agriGeneralParameterId, runId, userId);

		AgriGeneralParametersResponseDto response = (AgriGeneralParametersResponseDto) serviceResponse.get("response");
		String message = (String) serviceResponse.get("message");
		return apiResponse.commonResponseHandler(response, message, HttpStatus.OK);
	}

	@GetMapping("/{projectId}/agriGeneralParameters")
	public ResponseEntity<Object> getAgriGeneralParameters(@PathVariable Long projectId,
			@Nullable @RequestParam Long runId, HttpServletRequest httpRequest) {

		Long userId = (Long) httpRequest.getAttribute("userId");
		Map<String, Object> serviceResponse = agriGeneralParametersService.getAgriGeneralParameters(projectId, runId,
				userId);

		AgriGeneralParametersResponseDto response = (AgriGeneralParametersResponseDto) serviceResponse.get("response");
		String message = (String)serviceResponse.get("message");
		return apiResponse.commonResponseHandler(response, message,HttpStatus.OK);

	}
}
