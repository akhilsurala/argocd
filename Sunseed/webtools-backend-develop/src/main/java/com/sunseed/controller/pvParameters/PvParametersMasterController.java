package com.sunseed.controller.pvParameters;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.response.ApiResponse;
import com.sunseed.service.PvParametersService;

import io.micrometer.common.lang.Nullable;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1/project")
@RequiredArgsConstructor
public class PvParametersMasterController {

	private final ApiResponse apiResponse;
	private final PvParametersService pvParametersService;

	@GetMapping("/pvParameters/master")
	public ResponseEntity<Object> getMasterPvParameters(@Nullable @RequestParam(name = "mode", required = false) String mode) {

		Map<String, Object> response = pvParametersService.getMasterData(mode);
		return apiResponse.commonResponseHandler(response, "pvMasterData.fetched", HttpStatus.OK);
	}
}