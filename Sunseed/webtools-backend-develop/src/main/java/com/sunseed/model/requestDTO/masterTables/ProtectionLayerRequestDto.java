package com.sunseed.model.requestDTO.masterTables;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class ProtectionLayerRequestDto {

	@NotBlank(message = "layerName.cannotBe.nullOrBlank")
	@Pattern(regexp = "^(?!.*\\s\\s)[a-zA-Z0-9]+(?:\\s[a-zA-Z0-9]+)*$", message = "protectionLayer.name.invalid")
	private String name;
	
	@NotNull(message = "polysheets.not.null")
	private String polysheets;
    @NotNull(message = "linkToTexture.not.null")
	private String linkToTexture;
    @NotNull(message = "diffusionFraction.not.null")
    @DecimalMin(value = "0.0", inclusive = true, message = "diffusionFraction.min.value")
    @DecimalMax(value = "1.0", inclusive = true, message = "diffusionFraction.max.value")
	private Double diffusionFraction;
    
    @NotNull(message = "transmissionPercentage.not.null")
    @DecimalMin(value = "0.0", inclusive = true, message = "transmissionPercentage.min.value")
    @DecimalMax(value = "100.0", inclusive = true, message = "transmissionPercentage.max.value")
	private Double transmissionPercentage;
    
    @NotNull(message = "voidPercentage.not.null")
    @DecimalMin(value = "0.0", inclusive = true, message = "voidPercentage.min.value")
    @DecimalMax(value = "100.0", inclusive = true, message = "voidPercentage.max.value")
    private Double voidPercentage;
    
//    @NotNull(message = "opticalProperties.not.null")
    private OpticalRequestDto opticalProperties;
    
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

	
	@NotNull(message = "protectionLayer.hide.null")
	private Boolean hide;
}
