package com.sunseed.simtool.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.simtool.model.request.SimulationRequestDto;
import com.sunseed.simtool.model.response.SimulationResponseDto;
import com.sunseed.simtool.service.SimulationBlockwiseService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class SimulationBlockwiseController {

	private final SimulationBlockwiseService simulationBlockwiseService;

	@PostMapping("/simulation")
	private ResponseEntity<List<SimulationResponseDto>> createSimulationBlockwise(
			@Valid @RequestBody SimulationRequestDto simulationDto) {
		log.debug("Enter into createSimulationBlockwise");

		List<SimulationResponseDto> simulationResponseDtos = new ArrayList<>();

		for (Map<String, Object> runPayload : simulationDto.getRunPayload()) {
			try {
				SimulationResponseDto simulationResponseDto = simulationBlockwiseService.createSimulationBlockwise(
						runPayload, simulationDto.getProjectId(), simulationDto.getUserProfileId());
				simulationResponseDtos.add(simulationResponseDto);
			} catch (Exception e) {
				log.error("{}", e);
				SimulationResponseDto simulationResponseDto = new SimulationResponseDto();
				simulationResponseDto.setErrorMessage(e.getMessage());
				simulationResponseDto.setRunId(((Integer) runPayload.get("id")).longValue());
				simulationResponseDtos.add(simulationResponseDto);
			}
		}

		log.debug("Exit from createSimulationBlockwise");
		return new ResponseEntity<>(simulationResponseDtos, HttpStatus.CREATED);
	}
}
