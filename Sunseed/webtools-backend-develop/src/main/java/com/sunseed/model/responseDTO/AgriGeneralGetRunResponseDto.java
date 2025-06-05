package com.sunseed.model.responseDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sunseed.entity.BedParameter;
import com.sunseed.entity.Irrigation;
import com.sunseed.entity.SoilType;
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
public class AgriGeneralGetRunResponseDto {
	
	private Long id;
	private Long projectId;
	@JsonIgnore
	private Long runId;
	private List<AgriPvProtectionHeightResponseDto> agriPvProtectionHeight;

	@JsonIgnoreProperties("agriPvParameter")
	private Irrigation irrigationType;

	@JsonIgnoreProperties("agriPvParameter")
	private SoilType soilType;

//	@JsonIgnoreProperties("agriPvParameter")
	private TempControl tempControl;

	private Double trail;

	private Double minTemp;

	private Double maxTemp;

	private Boolean isMulching;

	@JsonIgnoreProperties("agriGeneralParameter")
	private BedParameter bedParameter;
	private String status;

}
