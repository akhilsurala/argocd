package com.sunseed.charts;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ChartsController {

	private final ChartsService chartsService;
	private final ApiResponse apiResponse;

	@PutMapping("/projects/{projectId}/runs/design-explorer")
	public ResponseEntity<Object> getAllRunsInputsOutputsForDesignExplorer(@PathVariable("projectId") Long projectId,
			@Valid @RequestBody ChartsRequestDto requestDto, HttpServletRequest request) {

		Long userId = (Long) request.getAttribute("userId");
		Map<String,Object> runs = chartsService.getAllRunsInOutDataForDesignExplorer(projectId,
				userId, requestDto.getRunIdList());
		return apiResponse.commonResponseHandler(runs, "run.in.out.data.design.explorer", HttpStatus.OK);
	}
}
