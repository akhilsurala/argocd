package com.sunseed.model.requestDTO.masterTables;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.AssertTrue;
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
public class CropRequestDto {

    @NotBlank(message = "cropName.cannotBe.blank")
    @Size(min = 5, max = 50, message = "pvModule.name.length.invalid")
	@Pattern(
	    regexp = "^(?!\\s)(?!.*\\s\\s)[a-zA-Z0-9._]+(?:\\s[a-zA-Z0-9._]+)*(?<!\\s)$",
	    message = "pvModule.name.invalid.characters")
    private String name;
    
    @NotNull(message = "vcMax.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "vcMax.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "vcMax.max.value")
    private Double vcMax;
    
    @NotNull(message = "jMax.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "jMax.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "jMax.max.value")
    private Double jMax;
    
    @NotNull(message = "cjMax.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "cjMax.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "cjMax.max.value")
    private Double cjMax;
    
    @NotNull(message = "hajMax.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "hajMax.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "hajMax.max.value")
    private Double hajMax;
    
    @NotNull(message = "alpha.not.null")
    @DecimalMin(value = "0.0", inclusive = true, message = "alpha.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "alpha.max.value")
    private Double alpha;
    
    @NotNull(message = "rd25.not.null")
    @DecimalMin(value = "0.0", inclusive = true, message = "rd25.min.value")
    @DecimalMax(value = "10.0", inclusive = true, message = "rd25.max.value")
    private Double rd25;
    
    @NotNull(message = "em.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "em.min.value")
    @DecimalMax(value = "1000000.0", inclusive = true, message = "em.max.value")
    private Double em;
    
    @NotNull(message = "io.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "io.min.value")
    @DecimalMax(value = "1000000.0", inclusive = true, message = "io.max.value")
    private Double io;
    
    @NotNull(message = "k.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "k.min.value")
    @DecimalMax(value = "1000000.0", inclusive = true, message = "k.max.value")
    private Double k;
    
    @NotNull(message = "b.not.null")
    @DecimalMin(value = "1.0", inclusive = true, message = "b.min.value")
    @DecimalMax(value = "1000000.0", inclusive = true, message = "b.max.value")
    private Double b;
    
    @NotNull(message = "opticalProperties.not.null")
    private OpticalRequestDto opticalProperties;
    @NotNull(message = "requiredDLI.not.null")
    @Min(value = 1, message = "requiredDLI.min.value")
    @Max(value = 100, message = "requiredDLI.max.value")
    private Long requiredDLI;
    
    @NotNull(message = "requiredPPFD.not.null")
    @Min(value = 10, message = "requiredPPFD.min.value")
    @Max(value = 1500, message = "requiredPPFD.max.value")
    private Long requiredPPFD;
    
    @NotNull(message = "harvestDays.not.null")
    @Min(value = 10, message = "harvestDays.min.value")
    @Max(value = 365, message = "harvestDays.max.value")
    private Long harvestDays;
    
    @JsonProperty("f1")
    @DecimalMin(value = "0.0", inclusive = true, message = "f1.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f1.max.value")
    private Double f1;
    
    @JsonProperty("f2")
    @DecimalMin(value = "0.0", inclusive = true, message = "f2.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f2.max.value")
    private Double f2;
    
    @JsonProperty("f3")
    @DecimalMin(value = "0.0", inclusive = true, message = "f3.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f3.max.value")
    private Double f3;
    
    @JsonProperty("f4")
    @DecimalMin(value = "0.0", inclusive = true, message = "f4.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f4.max.value")
    private Double f4;
    
    @JsonProperty("f5")
    @DecimalMin(value = "0.0", inclusive = true, message = "f5.min.value")
    @DecimalMax(value = "1000.0", inclusive = true, message = "f5.max.value")
    private Double f5;
    
    private Long minStage;

//  @NotNull(message = "maxStage.cannotBe.null")
    private Long maxStage;

    private Integer totalStagingCount;


    @Min(value= 1, message="crop.duration.minmax")
    @Max(value = 366, message = "crop.duration.minmax")
    private Integer duration;

    @NotNull(message = "crop.hide.null")
    private Boolean hide;
    
    @NotBlank(message = "cropLabel.cannotBe.blank")
    @Size(min = 5, max = 50, message = "pvModule.name.length.invalid")
	@Pattern(
	    regexp = "^(?!\\s)(?!.*\\s\\s)[a-zA-Z0-9._]+(?:\\s[a-zA-Z0-9._]+)*(?<!\\s)$",
	    message = "pvModule.name.invalid.characters")
    private String cropLabel;
    
    @NotNull(message = "hasPlantActualDate.cannotBe.null")
    private Boolean hasPlantActualDate;

//    @AssertTrue(message = "plantActualStartDate.mandatory.when.hasPlantActualDate.true")
//    private boolean isPlantActualStartDateValid() {
//        return !hasPlantActualDate || (plantActualStartDate != null);
//    }

    private String plantActualStartDate;

    @NotNull(message = "plantMaxAge.cannotBe.null")
    @Min(value = 0, message = "plantMaxAge.min.value")
    @Max(value = 5000, message = "plantMaxAge.max.value")
    private Integer plantMaxAge;

    @NotNull(message = "maxPlantsPerBed.cannotBe.null")
    @Min(value = 3, message = "maxPlantsPerBed.min.value")
    @Max(value = 50, message = "maxPlantsPerBed.max.value")
    private Integer maxPlantsPerBed;
}
