package com.sunseed.controller.graph;

import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.KeyDeltaGraphRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.PostProcessorGraphService;
import com.sunseed.service.RunService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class GraphController {
	
	
	private final PostProcessorGraphService graphService;
    private final ApiResponse apiResponse;
	
	@GetMapping("/project/{projectId}/withinRuns/2dGraph")
    public ResponseEntity<Object> getAllSimulationFromRunId(@PathVariable Long projectId,
    		@RequestParam Long runId, HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        HashMap<String, Object> simulationData = this.graphService.getAllSimulationFromRunId(projectId, runId, userId);

        return apiResponse.loginResponseHandler(simulationData, "run.fetched.successfully", HttpStatus.OK);
    }
	
	@PutMapping("/project/{projectId}/acrossRuns/keyDeltaGraph")
    public ResponseEntity<Object> getKeyDeltaGraph(@PathVariable Long projectId,
    @RequestBody KeyDeltaGraphRequestDto requestDto,
    HttpServletRequest request)  {

        Long userId = (Long) request.getAttribute("userId");
        HashMap<String, Object> deltaGraph = this.graphService.getAllCropData(projectId, requestDto.getRunId(), userId, requestDto.getCropId(), requestDto.getTypeGraph());

        return apiResponse.loginResponseHandler(deltaGraph, "run.fetched.successfully", HttpStatus.OK);
    }

}
