package com.sunseed.model.responseDTO.agriGeneralParameters;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IrrigationResponse {

	private Long id;
	private String name;
}
