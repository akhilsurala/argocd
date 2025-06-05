package com.sunseed.model.responseDTO;

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
public class BedParameterResponseDto {

	private Long id;

	private Double bedWidth;
	private Double bedHeight;
	private Double bedAngle;
	private Double bedAzimuth;
	private Double bedcc;
	private Double startPointOffset;
}
