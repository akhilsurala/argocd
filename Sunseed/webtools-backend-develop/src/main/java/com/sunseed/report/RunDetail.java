package com.sunseed.report;

import java.time.Instant;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.CropParameters;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvParameter;
import com.sunseed.entity.SimulatedRun;
import com.sunseed.enums.RunStatus;
import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
import com.sunseed.model.responseDTO.PvParametersResponseDto;

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
public class RunDetail {
	
	private Long id;
	private Long projectId;
	private String runName;
	private PreProcessorToggle preProcessorToggles;
	private PvParametersResponseDto pvParameters;
	private CropResponse cropParameters;
	private AgriGeneralResponseDto agriGeneralParameter;
	private EconomicParameterResponse economicParameters;
	private RunStatus runStatus;
	private Long progress;
	private Long simulatedId;
	private Instant createdAt;
	private Instant updatedAt;
	private Boolean variantExist;
	private Long cloneId;
	private Boolean isMaster;
	private Boolean agriControl;
	private Boolean pvControl;

}
