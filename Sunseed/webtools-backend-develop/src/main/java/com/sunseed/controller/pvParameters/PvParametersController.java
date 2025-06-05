package com.sunseed.controller.pvParameters;

import java.util.HashMap;
import java.util.Map;

import com.sunseed.enums.Toggle;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.PvParametersRequestDto;
import com.sunseed.model.responseDTO.PvParametersResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.PvParametersService;
import com.sunseed.service.RunCloneService;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class PvParametersController {

	private final ApiResponse apiResponse;
	private final PvParametersService pvParametersService;
	private final RunCloneService runCloneService;

	@PostMapping("/{projectId}/pvParameters")
	public ResponseEntity<Object> addPvParameters(
			@Validated(ValidationGroups.ToggleGroup.class) @RequestBody PvParametersRequestDto request,
			@PathVariable Long projectId, @RequestParam("toggle") String toggle, @Nullable @RequestParam Long runId,
			@Nullable @RequestParam Boolean isCloned, @Nullable @RequestParam Boolean isMaster,HttpServletRequest servletRequest) {
		System.out.println("pv parameters request object: " + request);

		Long userId = (Long) servletRequest.getAttribute("userId");
		Map<String, Object> serviceResponse = new HashMap<>();

		if (isCloned != null && isCloned == true) {

			serviceResponse = runCloneService.createCloneForGivenRun(request,projectId,toggle,runId,userId,isMaster);
		} else {

			final String callFor = "create";
			serviceResponse = pvParametersService.addOrUpdatePvParametersWithToggle(request, projectId, toggle, runId,
					userId, callFor, null);
		}

		PvParametersResponseDto response = (PvParametersResponseDto) serviceResponse.get("response");
		String message = (String) serviceResponse.get("message");
		HttpStatus httpStatus = (HttpStatus) serviceResponse.get("httpStatus");
		return apiResponse.commonResponseHandler(response, message, httpStatus);
	}

	@PutMapping("/{projectId}/pvParameters/{pvParameterId}")
	public ResponseEntity<Object> updatePvParameters(
			@Validated(ValidationGroups.ToggleGroup.class) @RequestBody PvParametersRequestDto request,
			@PathVariable Long pvParameterId, @PathVariable Long projectId, @RequestParam("toggle") String toggle,
			@Nullable @RequestParam Long runId, HttpServletRequest servletRequest) {

		Long userId = (Long) servletRequest.getAttribute("userId");
		final String callFor = "update";

		Map<String, Object> serviceResponse = pvParametersService.addOrUpdatePvParametersWithToggle(request, projectId,
				toggle, runId, userId, callFor, pvParameterId);
		PvParametersResponseDto response = (PvParametersResponseDto) serviceResponse.get("response");
		String message = (String) serviceResponse.get("message");
		HttpStatus httpStatus = (HttpStatus) serviceResponse.get("httpStatus");
		return apiResponse.commonResponseHandler(response, message, httpStatus);
	}

	@GetMapping("/{projectId}/pvParameters")
	public ResponseEntity<Object> getPvParameters(@PathVariable Long projectId, @Nullable @RequestParam Long runId,
			@Nullable @RequestParam Boolean isCloned, HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Map<String, Object> serviceResponse = new HashMap<>();

		// if isCloned is true then need to extract pv parameters from given master
		// runId
		if (isCloned != null && isCloned == true) {
			serviceResponse = runCloneService.getPvParametersWithToggle(userId, projectId, runId);
		} else {
			serviceResponse = pvParametersService.getPvParametersWithToggle(userId, projectId, runId);
		}

		PvParametersResponseDto response = (PvParametersResponseDto) serviceResponse.get("response");
		String message = (String) serviceResponse.get("message");
		HttpStatus httpStatus = (HttpStatus) serviceResponse.get("httpStatus");
		return apiResponse.commonResponseHandler(response, message, httpStatus);
	}
}
