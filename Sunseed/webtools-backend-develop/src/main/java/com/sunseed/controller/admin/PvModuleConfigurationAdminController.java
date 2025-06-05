package com.sunseed.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.masterTables.PvModuleConfigurationRequestDto;
import com.sunseed.model.responseDTO.pvParameters.PvModuleConfigurationResponse;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.PvModuleConfigurationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class PvModuleConfigurationAdminController {

	private final ApiResponse apiResponse;
	private final PvModuleConfigurationService pvModuleConfigurationService;

	@PostMapping("/pvModuleConfiguration")
	public ResponseEntity<Object> addPvModuleConfiguration(
			@Valid @RequestBody PvModuleConfigurationRequestDto requestDto) {

		PvModuleConfigurationResponse response = pvModuleConfigurationService.addPvModuleConfiguration(requestDto);

		return apiResponse.ResponseHandler(true, "pvModuleConfiguration.added", HttpStatus.OK, response);
	}

	@PutMapping("/pvModuleConfiguration/{pvModuleConfigurationId}")
	public ResponseEntity<Object> updatePvModuleConfiguration(
			@Valid @RequestBody PvModuleConfigurationRequestDto requestDto,
			@PathVariable("pvModuleConfigurationId") Long pvModuleConfigurationId) {

		PvModuleConfigurationResponse response = pvModuleConfigurationService
				.updatePvModuleConfiguration(requestDto, pvModuleConfigurationId);

		return apiResponse.ResponseHandler(true, "pvModuleConfiguration.updated", HttpStatus.OK, response);
	}

	@DeleteMapping("/pvModuleConfiguration/{pvModuleConfigurationId}")
	public ResponseEntity<Object> deletePvModuleConfiguration(
			@PathVariable("pvModuleConfigurationId") Long pvModuleConfigurationId) {

		pvModuleConfigurationService.deletePvModuleConfiguration(pvModuleConfigurationId);
		return apiResponse.ResponseHandler(true, "pvModuleConfiguration.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/pvModuleConfigurations")
	public ResponseEntity<Object> getPvModuleConfigurations(@Nullable @RequestParam(name = "search", required = false) String search) {

		List<PvModuleConfigurationResponse> pvModuleConfigurations = pvModuleConfigurationService.getPvModuleConfigurations(search);

		return apiResponse.ResponseHandler(true, "pvModuleConfiguration.fetched", HttpStatus.OK,
				pvModuleConfigurations);
	}

	@GetMapping("/pvModuleConfiguration/{pvModuleConfigurationId}")
	public ResponseEntity<Object> getPvModuleConfigurationById(
			@PathVariable("pvModuleConfigurationId") Long pvModuleConfigurationId) {
		PvModuleConfigurationResponse pvModuleConfiguration = pvModuleConfigurationService
				.getPvModuleConfigurationById(pvModuleConfigurationId);
		return apiResponse.ResponseHandler(true, "pvModuleConfiguration.fetched", HttpStatus.OK, pvModuleConfiguration);
	}
}
