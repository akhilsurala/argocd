package com.sunseed.model.responseDTO;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sunseed.entity.PreProcessorToggle;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RunResponseToggleDto {

	private Long id;

	private String runName;
	private Long projectId;

	private PreProcessorToggle preProcessorToggle;

	private PvParametersResponseDto pvParameters;

	private CropParametersResponseDto cropParameters;

	@JsonManagedReference
	private AgriGeneralGetRunResponseDto agriGeneralParameters;

	private EconomicParametersResponseDto economicParameters;

	private String runStatus;
	private Long progress;
	private Long simulatedId;
	private Instant createdAt;
	private Instant updatedAt;
	private Boolean variantExist;
	private Long cloneId;
	private Boolean isMaster;
	private boolean agriControl;
	private boolean pvControl;

}
