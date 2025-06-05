package com.sunseed.model.responseDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CropTypeResponse {
	private Long cropId1;
	private Long o1;
	private Long s1;
	private Long o2;
	
	private Long optionalCropType;
	private Long optionalO1;
	private Long optionalS1;
	private Long optionalO2;
}