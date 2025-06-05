package com.sunseed.model.requestDTO.masterTables;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PvModuleRequestDto {

	@NotBlank(message = "pvModule.name.empty")
	@Size(min = 5, max = 50, message = "pvModule.name.length.invalid")
	@Pattern(
	    regexp = "^(?!\\s)(?!.*\\s\\s)[a-zA-Z0-9._]+(?:\\s[a-zA-Z0-9._]+)*(?<!\\s)$",
	    message = "pvModule.name.invalid.characters")
	private String name;
	
	@NotNull(message = "pvModule.length.null")
	private Double length;
	@NotNull(message = "pvModule.width.null")
	private Double width;
	@NotNull(message = "pvModule.hide.null")
	private Boolean hide;
	
	@NotNull(message = "manufacturerName.not.null")
	@Size(min = 2, max = 30, message = "manufacturerName.length.invalid")
	@Pattern(
	    regexp = "^(?!\\s)(?!.*\\s\\s)[a-zA-Z0-9._]+(?:\\s[a-zA-Z0-9._]+)*(?<!\\s)$",
	    message = "manufacturerName.invalid.characters")
    private String manufacturerName;

    @NotNull(message = "moduleName.not.null")
    private String moduleName;

    @NotNull(message = "shortcode.not.null")
    private String shortcode;

    @NotNull(message = "moduleTech.not.null")
    private String moduleTech;

    @NotNull(message = "linkToDataSheet.not.null")
    private String linkToDataSheet;

    @Min(value = 1, message = "numCellX.min.value")
    @Max(value = 24, message = "numCellX.max.value")
    private Integer numCellX;
    
    @Min(value = 1, message = "numCellY.min.value")
    @Max(value = 100, message = "numCellY.max.value")
    private Integer numCellY;
    
    @Min(value = 1, message = "longerSide.min.value")
    @Max(value = 4000, message = "longerSide.max.value")
    private Integer longerSide;
    
    @Min(value = 1, message = "shorterSide.min.value")
    @Max(value = 2000, message = "shorterSide.max.value")
    private Integer shorterSide;
    
    @Min(value = 1, message = "thickness.min.value")
    @Max(value = 50, message = "thickness.max.value")
    private Integer thickness;

    private Float voidRatio;
    private Float xCell;
    private Float yCell;
    private Float xCellGap;
    private Float yCellGap;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "vMap.min.value")
    @DecimalMax(value = "200.0", inclusive = true, message = "vMap.max.value")
    private Float vMap;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "iMap.min.value")
    @DecimalMax(value = "100.0", inclusive = true, message = "iMap.max.value")
    private Float iMap;
    private Float idc0;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "pdc0.min.value")
    @DecimalMax(value = "2000.0", inclusive = true, message = "pdc0.max.value")
    private Float pdc0;
    @DecimalMin(value = "0.0", inclusive = true, message = "nEffective.min.value")
    @DecimalMax(value = "100.0", inclusive = true, message = "nEffective.max.value")
    private Float nEffective;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "vOc.min.value")
    @DecimalMax(value = "200.0", inclusive = true, message = "vOc.max.value")
    private Float vOc;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "iSc.min.value")
    @DecimalMax(value = "100.0", inclusive = true, message = "iSc.max.value")
    private Float iSc;
    
    @DecimalMin(value = "0.01", inclusive = true, message = "alphaSc.min.value")
    @DecimalMax(value = "0.999", inclusive = true, message = "alphaSc.max.value")
    private Float alphaSc;
    
    @DecimalMin(value = "-1.0", inclusive = true, message = "betaVoc.min.value")
    @DecimalMax(value = "0.0", inclusive = true, message = "betaVoc.max.value")
    private Float betaVoc;
    
    @DecimalMin(value = "-1.0", inclusive = true, message = "gammaPdc.min.value")
    @DecimalMax(value = "0.0", inclusive = true, message = "gammaPdc.max.value")
    private Float gammaPdc;
    
    @DecimalMin(value = "5.0", inclusive = true, message = "temRef.min.value")
    @DecimalMax(value = "35.0", inclusive = true, message = "temRef.max.value")
    private Float temRef;
    
    @DecimalMin(value = "0.0", inclusive = true, message = "radSun.min.value")
    @DecimalMax(value = "10.0", inclusive = true, message = "radSun.max.value")
    private Float radSun;
    
    // New field for optical properties
    @NotNull(message = "opticalProperties.not.null")
    private List<PvModuleOpticalRequestDto> opticalProperties;
    
    @JsonProperty("f1")
    @DecimalMin(value = "-1000.0", inclusive = true, message = "f1.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f1.max.value")
    private Double f1;
    
    @JsonProperty("f2")
    @DecimalMin(value = "-1000.0", inclusive = true, message = "f2.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f2.max.value")
    private Double f2;
    
    @JsonProperty("f3")
    @DecimalMin(value = "-1000.0", inclusive = true, message = "f3.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f3.max.value")
    private Double f3;
    
    @JsonProperty("f4")
    @DecimalMin(value = "-1000.0", inclusive = true, message = "f4.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f4.max.value")
    private Double f4;
    
    @JsonProperty("f5")
    @DecimalMin(value = "-1000.0", inclusive = true, message = "f5.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f5.max.value")
    private Double f5;
}
