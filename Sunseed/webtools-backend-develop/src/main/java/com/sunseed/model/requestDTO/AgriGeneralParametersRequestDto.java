package com.sunseed.model.requestDTO;

import java.util.ArrayList;
import java.util.List;

import com.sunseed.model.ValidationGroups;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class AgriGeneralParametersRequestDto {

//	@NotNull(message = "select.value", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	List<AgriPvProtectionHeightRequestDto> agriPvProtectionHeight;

	@NotNull(message = "select.value", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Long irrigationTypeId;

//	@NotNull(message = "select.value", groups = ValidationGroups.AgriGeneralParametersGroup.class)
//	private Long soilId;

	@NotNull(message = "field.required", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private String tempControl;

	@NotNull(message = "field.required", groups = ValidationGroups.TrailMinMaxGroup.class)
	@DecimalMin(value = "1.0", message = "trail.range", groups = ValidationGroups.TrailMinMaxGroup.class)
	@DecimalMax(value = "50.0", message = "trail.range", groups = ValidationGroups.TrailMinMaxGroup.class)
	private Double trail;

	@NotNull(message = "field.required",groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	@DecimalMin(value = "0.0", message = "temp.range", groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	@DecimalMax(value = "50.0", message = "temp.range", groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	private Double minTemp;

	@NotNull(message = "field.required",groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	@DecimalMin(value = "0.0", message = "temp.range", groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	@DecimalMax(value = "50.0", message = "temp.range", groups = { ValidationGroups.AbsoluteMinMaxGroup.class,
			ValidationGroups.TrailMinMaxGroup.class })
	private Double maxTemp;

	@NotNull(message = "field.mulching", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	@Builder.Default
	private Boolean isMulching = false;

	@NotNull(message = "field.bedWidth", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	@DecimalMin(value = "100", message = "bedWidth.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
//	@DecimalMax(value = "500", message = "bedWidth.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Double bedWidth;

	@NotNull(message = "field.required", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	@DecimalMin(value = "0", message = "bedHeight.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
//	@DecimalMax(value = "500", message = "bedHeight.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Double bedHeight;

	@NotNull(message = "field.required", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	@DecimalMin(value = "0.0", message = "bedAngle.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	@DecimalMax(value = "180.0", message = "bedAngle.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Double bedAngle;

	@NotNull(message = "field.required", groups = ValidationGroups.OffsetGroup.class)
//	@Min(value = 0, message = "bedAzimuth.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
//	@Max(value = 360, message = "bedAzimuth.range", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Double bedAzimuth;

	@NotNull(message = "field.required", groups = ValidationGroups.AgriGeneralParametersGroup.class)
	private Double bedcc;

	@NotNull(message = "field.required", groups = ValidationGroups.OffsetGroup.class)
	// @DecimalMin(value = "0.0", message = "start.point.offset.range", groups = ValidationGroups.OffsetGroup.class)
	private Double startPointOffset;

	@Builder.Default
	private List<Long> protectionDelete = new ArrayList<>();
}
