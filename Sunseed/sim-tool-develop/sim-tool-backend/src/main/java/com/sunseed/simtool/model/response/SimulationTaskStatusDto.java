package com.sunseed.simtool.model.response;

import com.sunseed.simtool.constant.Status;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SimulationTaskStatusDto {

	private Status status;
	private Long taskCount;
	
	public SimulationTaskStatusDto(Status status, Long taskCount) {
		super();
		this.status = status;
		this.taskCount = taskCount;
	}
}
