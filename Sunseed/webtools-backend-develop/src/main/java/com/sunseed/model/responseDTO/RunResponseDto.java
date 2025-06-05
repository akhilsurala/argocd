package com.sunseed.model.responseDTO;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
public class RunResponseDto {
	
private Long id;
	
	private String  runName;
	private Long projectId;

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

}
