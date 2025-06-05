package com.sunseed.simtool.model.response;

import java.time.LocalDate;
import java.util.Map;

import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;

import lombok.Data;

@Data
public class SimulationResponseDto {

	private Long id;
	private Long userProfileId;
	private Long projectId;
	private Long runId;
	private Status status;
	private Long taskCount;
	private Long completedTaskCount;
	private Boolean withTracking;
	private SimulationType simulationType;
	private Map<String, Object> runPayload;
	private LocalDate startDate;
	private LocalDate endDate;
	private String errorMessage;
}
