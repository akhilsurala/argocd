package com.sunseed.controller.run;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.sunseed.charts.ChartsRequestDto;
import com.sunseed.entity.Runs;
import com.sunseed.exceptions.InvalidDataException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.RunService;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class RunController {

	private final RunService runService;
	private final ApiResponse apiResponse;

	@Autowired
	private WebClientHelper webClientHelper;

	@GetMapping("/project/{projectId}/runs")
	public ResponseEntity<Object> getRuns(@PathVariable Long projectId,
			@RequestParam(name = "bay", required = false) String bay, @Nullable @RequestParam("runId") Long runId,
			@Nullable @RequestParam("searchText") String searchText, HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Map<String, Object> runs = this.runService.getAllRuns(projectId, bay, userId, runId, searchText);

		return apiResponse.loginResponseHandler(runs, "run.fetched.successfully", HttpStatus.OK);
	}

	// *********** simulate run api ***************
	@PutMapping("/project/{projectId}/runs/simulate")
	public ResponseEntity<Object> simulateRun(@PathVariable Long projectId, @RequestBody Map<String, List<Long>> json,
			HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		List<Long> runIds = json.get("runId");
		if (userId == null) {
			throw new UnprocessableException("user.id.not.found");
		}
		// Runs run = this.runService.simulateRun(projectId, userId, runId);
		// return apiResponse.loginResponseHandler(run, "run.simulated.successfully",
		// HttpStatus.OK);
		// List<SimulationResponseDto>
		// simulationResponse=simulationService.postSimulation(runIds,projectId);
		List<SimulationResponseDto> simulationResponse = runService.postSimulation(runIds, projectId, userId);

		return apiResponse.loginResponseHandler(simulationResponse, "run.simulated.successfully", HttpStatus.OK);
	}

	// change status of simulated run
	// Cancel, Pause, Resume
	@PutMapping("/run/{runId}/status")
	public ResponseEntity<Object> updateSimulatedStatus(@RequestBody Map<String, String> json,
			@PathVariable Long runId) {
		String status = json.get("status");
		if (status == null || status.trim().isEmpty()) {
			throw new InvalidDataException("invalid.data");
		}
		if (status.equalsIgnoreCase("cancel") || status.equalsIgnoreCase("pause")
				|| status.equalsIgnoreCase("resume")) {
			Map<String, List<SimulationTaskStatusDto>> data = runService.updateSimulatedRunStatus(status, runId);
			return apiResponse.loginResponseHandler(data, "status.changed", HttpStatus.OK);
		}
		throw new UnprocessableException("status.invalid");
	}

	@GetMapping("/simulation")
	private ResponseEntity<Map<String, Object>> getSimulation(@RequestParam Long id) {

		log.debug("Enter into getSimulation");
		Map<String, Object> simulation = webClientHelper.getSimulation(id);
		log.debug("Exit from getSimulation");
		return new ResponseEntity<>(simulation, HttpStatus.OK);
	}

	@DeleteMapping("/project/{projectId}/run/{runId}")
	public ResponseEntity<Object> deleteRun(@PathVariable("projectId") Long projectId,
			@PathVariable("runId") Long runId, HttpServletRequest request) {
		log.debug("Entered into deleteRun() method");
		Long userId = (Long) request.getAttribute("userId");
		runService.deleteRun(userId, projectId, runId);
		return apiResponse.commonResponseHandler(null, "run.deleted.successfully", HttpStatus.OK);
	}

	@PutMapping("/projects/{projectId}/runs/{runId}/agri-pv-control")
	public ResponseEntity<Object> updateAgriPvControlStatusOfRun(@PathVariable("projectId") Long projectId,
			@PathVariable("runId") Long runId, @Nullable @RequestParam("agriControl") Boolean agriControl,
			@Nullable @RequestParam("pvControl") Boolean pvControl, HttpServletRequest request) {
		log.debug("Entered into updateAgriPvControlStatusOfRun() method");
		Long userId = (Long) request.getAttribute("userId");
		Map<String, Object> serviceResponse = runService.updateAgriPvControlStatusOfRun(projectId, runId, userId,
				agriControl, pvControl);
		Runs run = (Runs) serviceResponse.get("run");
		String message = (String) serviceResponse.get("message");
		return apiResponse.commonResponseHandler(run, message, HttpStatus.OK);
	}

	@PutMapping("/projects/{projectId}/runs/get-run-names")
	public ResponseEntity<Object> getRunNames(@PathVariable("projectId") Long projectId,
			@Valid @RequestBody ChartsRequestDto requestDto, HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Map<String,Object> runs = runService.getRunNames(projectId,
				userId, requestDto.getRunIdList());
		return apiResponse.commonResponseHandler(runs, "run.names.fetched", HttpStatus.OK);
	}

	// get scenes from simtool
	@GetMapping("/project/{projectId}/runs/{runId}/scenes")
	public ResponseEntity<Object> getScenesForRun(@PathVariable("projectId") Long projectId,
												  @PathVariable("runId") Long runId, HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		com.sunseed.projection.SceneResponse scenes = runService.getAllScenesForRun(projectId, runId, userId);
		return apiResponse.loginResponseHandler(scenes, "scenes.fetched.successfully", HttpStatus.OK);
	}


}
