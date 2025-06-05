package com.sunseed.controller.run;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.entity.Runs;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.RunCloneService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RunVariantController {

	private final ApiResponse apiResponse;
	private final RunCloneService runCloneService;

	@GetMapping("/project/{projectId}/runs/{runId}/variants")
	public ResponseEntity<Object> getVariantRuns(@PathVariable Long projectId, @PathVariable Long runId,
			HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Map<String, Object> runs = this.runCloneService.getAllVariantRuns(projectId, runId, userId);

		return apiResponse.loginResponseHandler(runs, "variant.run.fetched.successfully", HttpStatus.OK);
	}

	@PutMapping("/project/{projectId}/runs/{runId}/updateToMaster")
	public ResponseEntity<Object> updateRunMasterStatus(@PathVariable Long projectId, @PathVariable Long runId,
			HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Runs run = runCloneService.updateRunMasterStatus(projectId, runId, userId);
		return apiResponse.commonResponseHandler(run, "run.status.master", HttpStatus.OK);
	}


}
