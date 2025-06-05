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

public class ModeOfPvOperationResponse {

	private Long id;
	private String name;
}
