package com.sunseed.model.responseDTO.pvParameters;

import java.time.Instant;

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
public class PvModuleConfigurationResponse {

	private Long id;
	private String name;
	private int ordering;
	private int numberOfModules;
	private String typeOfModule;
	private Boolean hide;
	private Instant createdAt;
	private Instant updatedAt;
}
