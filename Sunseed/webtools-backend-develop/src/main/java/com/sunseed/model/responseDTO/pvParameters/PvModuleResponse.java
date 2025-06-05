package com.sunseed.model.responseDTO.pvParameters;

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
public class PvModuleResponse {

	private Long id;
	private String name;
	private Double length;
	private Double width;
}
