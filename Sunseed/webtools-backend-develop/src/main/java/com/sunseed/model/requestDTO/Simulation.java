package com.sunseed.model.requestDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Simulation {
	
	private Long id;
	private Long userProfileId;
	private Long projectId;
	private Long runId;
//	private Boolean isCompleted;
	private Long taskCount;
	private Long completedTaskCount;
	private String status;
//	private Boolean withTracking;

}
