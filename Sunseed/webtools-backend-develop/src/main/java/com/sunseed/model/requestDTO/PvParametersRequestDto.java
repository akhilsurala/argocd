package com.sunseed.model.requestDTO;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.model.ValidationGroups;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class PvParametersRequestDto {

	// toggle data
	@NotBlank(message = "pvParameter.runName.required", groups = ValidationGroups.ToggleGroup.class)
	@Size(max = 20, message = "pvParameter.runName.size", groups = ValidationGroups.ToggleGroup.class)
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_ ]*$", message = "pvParameter.runName.pattern", groups = ValidationGroups.ToggleGroup.class)
	private String runName;

	@NotNull(message = "pvParameter.pitchOfRowsRequired", groups = ValidationGroups.ToggleGroup.class)
	private Double pitchOfRows;

	@NotNull(message = "pvParameter.azimuthRequired", groups = ValidationGroups.ToggleGroup.class)
	@Min(value = 0, message = "azimuth.negative", groups = ValidationGroups.ToggleGroup.class)
	@Max(value = 360, message = "azimuth.greater", groups = ValidationGroups.ToggleGroup.class)
	private Double azimuth;

	@NotNull(message = "pvParameter.lengthOfOneRowRequired", groups = ValidationGroups.ToggleGroup.class)
	private Double lengthOfOneRow;

	// pv parameters data
	private Double tiltIfFt;
	private Double maxAngleOfTracking;
	private String moduleMaskPattern;
	
//	@NotNull(message = "xCoordinate.empty",groups = ValidationGroups.PvParametersGroup.class)
	@JsonProperty("XCoordinate")
	private Double XCoordinate;
	
//	@NotNull(message = "yCoordinate.empty",groups = ValidationGroups.PvParametersGroup.class)
	@JsonProperty("YCoordinate")
	private Double YCoordinate;

	@NotNull(message = "pvParameter.gapBetweenModulesRequired", groups = ValidationGroups.PvParametersGroup.class)
	private Double gapBetweenModules;

	@NotNull(message = "pvParameter.heightRequired", groups = ValidationGroups.PvParametersGroup.class)
	private Double height;

	@NotNull(message = "pvParameter.PvModuleRequired", groups = ValidationGroups.PvParametersGroup.class)
	private Long pvModuleId;

	@NotNull(message = "pvParameter.modeOfOperationRequired", groups = ValidationGroups.PvParametersGroup.class)
	private Long modeOfOperationId;

	@NotNull(message = "pvParameter.moduleConfigsRequired", groups = ValidationGroups.PvParametersGroup.class)
	private List<Long> moduleConfigId;

	private PreProcessorStatus status;
	
	@NotNull(message = "select.value", groups = ValidationGroups.PvParametersGroup.class)
	private Long soilId;
}
