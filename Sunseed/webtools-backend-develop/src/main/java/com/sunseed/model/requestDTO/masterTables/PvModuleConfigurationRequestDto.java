package com.sunseed.model.requestDTO.masterTables;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
public class PvModuleConfigurationRequestDto {

	@NotBlank(message = "empty.pvModuleConfigName")
	private String name;

	@Positive(message = "empty.pvModuleConfigNumberOfModules")
	private Integer numberOfModules;

	@Positive(message = "empty.ordering.pvModuleConfig")
	private Integer ordering;

	@NotBlank(message = "empty.pvModuleConfigType")
	private String typeOfModule;

	@NotNull(message = "pvModuleConfiguration.hide.null")
	private Boolean hide;
}
