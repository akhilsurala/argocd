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
public class AgriPvProtectionHeightResponseDto {

	private Long agriPvProtectionHeightId;
	private Long protectionId;
	private Double height;
	private String protectionLayerName;
}
