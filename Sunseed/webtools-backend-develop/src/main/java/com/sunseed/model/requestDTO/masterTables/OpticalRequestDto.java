package com.sunseed.model.requestDTO.masterTables;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
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
public class OpticalRequestDto {
	
//	@NotNull(message = "type.not.null")
//    private String type; // Expected values: "front" or "back"

	@DecimalMin(value = "0.0", inclusive = true, message = "reflectance_PAR.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "reflectance_PAR.max.value")
    private Double reflectance_PAR;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "reflectance_NIR.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "reflectance_NIR.max.value")
    private Double reflectance_NIR;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "transmissivity_PAR.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "transmissivity_PAR.max.value")
    private Double transmissivity_PAR;
	
	@DecimalMin(value = "0.0", inclusive = true, message = "transmissivity_NIR.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "transmissivity_NIR.max.value")
    private Double transmissivity_NIR;

//    private MultipartFile reflectanceFile; // File input for reflectance


}
