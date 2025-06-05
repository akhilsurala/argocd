package com.sunseed.report;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.response.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class RunReportController {
	
	private final RunReportService runOutputService;
	private final ApiResponse apiResponse;
	
	@GetMapping("/project/{projectId}/runs/report")
	public ResponseEntity<Object> getRunReport(@PathVariable("projectId") Long projectId, @RequestParam("runIds") List<Long> runIds,
			HttpServletRequest servletRequest){
		
		Long userId = (Long) servletRequest.getAttribute("userId");
		Map<String,Object> data = runOutputService.getRunReport(projectId,runIds,userId);
		return apiResponse.commonResponseHandler(data, "run.report.fetched", HttpStatus.OK);
	}

}
