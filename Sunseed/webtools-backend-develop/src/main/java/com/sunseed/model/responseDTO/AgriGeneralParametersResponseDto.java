package com.sunseed.model.responseDTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgriGeneralParametersResponseDto {

	private Long id;
	private Long projectId;
	private Long runId;
	private List<AgriPvProtectionHeightResponseDto> agriPvProtectionHeight;

	private Long irrigationType;

	private Long soilId;

	private String tempControl;

	private Double trail;

	private Double minTemp;

	private Double maxTemp;

	private Boolean isMulching;

	private BedParameterResponseDto bedParameter;
	private String status;
	private Long cloneId;
	private Boolean isMaster;
}
