package com.sunseed.model.responseDTO.agriGeneralParameters;

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
public class SoilTypeResponse {

	private Long id;
	private String name;
	private String soilPicturePath;
}
