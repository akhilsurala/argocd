package com.sunseed.report;

import java.util.List;

import com.sunseed.entity.AgriPvProtectionHeight;
import com.sunseed.entity.BedParameter;
import com.sunseed.entity.Irrigation;
import com.sunseed.entity.SoilType;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.TempControl;

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
public class AgriGeneralResponseDto {

	private Long id;
	private Long projectId;
	private Long runId;
	private List<AgriPvProtectionHeight> agriPvProtectionHeight;

	private Irrigation irrigationType;

	private SoilType soilType;

	private TempControl tempControl;

	private Double trail;

	private Double minTemp;

	private Double maxTemp;

	private Boolean isMulching;

	private BedParameter bedParameter;
	private PreProcessorStatus status;
	private Long cloneId;
	private Boolean isMaster;
}

