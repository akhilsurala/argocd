package com.sunseed.report;

import java.time.Instant;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.SimulatedRun;
import com.sunseed.enums.RunStatus;

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
public class RunProjectionForReport {

	private Long id;
	private Long projectId;
	private String runName;
	private PreProcessorToggle preProcessorToggles;
	private PvParameter pvParameters;
	private CropParameters cropParameters;
	private AgriGeneralParameter agriGeneralParameter;
	private EconomicParameters economicParameters;
	private RunStatus runStatus;
	private Long progress;
	private SimulatedRun simulatedRun;
	private Instant createdAt;
	private Instant updatedAt;
	private Boolean variantExist;
	private Long cloneId;
	private Boolean isMaster;
	private Boolean agriControl;
	private Boolean pvControl;
	
	public RunProjectionForReport(Long id, Long projectId, String runName, PreProcessorToggle preProcessorToggles,
			PvParameter pvParameters, CropParameters cropParameters, RunStatus runStatus, SimulatedRun simulatedRun,
			Instant createdAt, Instant updatedAt, AgriGeneralParameter agriGeneralParameter
			, EconomicParameters economicParameters) {
		this.id = id;
		this.projectId = projectId;
		this.runName = runName;
		this.preProcessorToggles = preProcessorToggles;
		this.pvParameters = pvParameters;
		this.cropParameters = cropParameters;
		this.agriGeneralParameter = agriGeneralParameter;
		this.economicParameters = economicParameters;
		this.runStatus = runStatus;
		this.simulatedRun = simulatedRun;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
	}
	
}
