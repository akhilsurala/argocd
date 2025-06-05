package com.sunseed.model.responseDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvModule;
import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.entity.SoilType;
import com.sunseed.enums.PreProcessorStatus;

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
public class PvParametersResponseDto {

	private Long id;
	private Long runId;
	private Long projectId;
	private Double tiltIfFt;
	private Double maxAngleOfTracking;
	private String moduleMaskPattern;
	private Double gapBetweenModules;
	private Double height;
	
	@JsonProperty("XCoordinate")
	private Double XCoordinate;
	
	@JsonProperty("YCoordinate")
	private Double YCoordinate;
	
	private PreProcessorStatus status;
	private PvModule pvModule;
	private ModeOfPvOperation modeOfOperationId;
	private List<PvModuleConfiguration> moduleConfigs;
	private PreProcessorToggle preProcessorToggle;
	private Long cloneId;
	private Boolean isMaster;
	private SoilType soilType;
}
