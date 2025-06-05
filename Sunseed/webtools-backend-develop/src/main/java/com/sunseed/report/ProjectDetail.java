package com.sunseed.report;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProjectDetail {

	private Long projectId;
	private String projectName;
	private String latitude;
	private String longitude;
	private Instant createdAt;
	private Instant updatedAt;
	private Long runInHoldingBay;
	private Long runInRunningBay;
}

