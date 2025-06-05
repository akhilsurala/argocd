package com.sunseed.model.requestDTO.masterTables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class TypeOfIrrigationRequestDto {

	@NotBlank(message = "irrigationType.cannotBe.blank")
	private String name;
	
	@NotNull(message = "irrigationType.hide.null")
	private Boolean hide;
}
