package com.sunseed.model.responseDTO;

import com.sunseed.entity.Crop;

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
public class CropResponse {
	private Long id;
	private String name;
	private Integer duration;
	private Crop meta;
}
