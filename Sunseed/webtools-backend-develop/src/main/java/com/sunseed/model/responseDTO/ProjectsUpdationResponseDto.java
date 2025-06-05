package com.sunseed.model.responseDTO;

import com.sunseed.model.Coordinates;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProjectsUpdationResponseDto {

	private Long projectId;
	private String projectName;
	private String latitude;
	private String longitude;
	private Coordinates[] polygonCoordinates;
	private String projectStatus;
	private String comments;
	private Double[] offSetPoint;
	private List<Long> runIds;
}
