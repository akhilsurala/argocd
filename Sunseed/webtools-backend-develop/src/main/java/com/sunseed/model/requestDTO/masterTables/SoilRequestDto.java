package com.sunseed.model.requestDTO.masterTables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class SoilRequestDto {

	@NotBlank(message = "soilName.cannotBe.blank")
	@Size(min = 4, max = 32, message = "name.length.invalid")
	@Pattern(regexp = "^(?!\\s)[a-zA-Z0-9]+(?:\\s[a-zA-Z0-9]+)*(?<!\\s)$",
	    message = "name.invalid.characters")
	private String name;
	
	@NotNull(message = "soil.hide.null")
	private Boolean hide;
	
	@NotNull(message = "opticalProperties.not.null")
    private OpticalRequestDto opticalProperties;
	
}
