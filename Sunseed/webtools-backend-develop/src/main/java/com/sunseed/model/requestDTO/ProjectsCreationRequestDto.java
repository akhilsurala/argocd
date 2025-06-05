package com.sunseed.model.requestDTO;

import java.util.List;

import com.sunseed.model.Coordinates;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class ProjectsCreationRequestDto {

	@NotBlank(message = "project.name.empty")
	@Size(min = 5, message = "project.name.tooShort")
	@Size(max = 30, message = "project.name.tooLong")
	@Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9_ ]*$", message = "project.name.invalid")
	private String projectName;
	
	@NotBlank(message = "latitude.is.empty")
	@Pattern(regexp = "^-?\\d+\\.\\d+$", message = "project.location.invalid")
	private String latitude;
	
	@NotBlank(message = "longitude.is.empty")
	@Pattern(regexp = "^-?\\d+\\.\\d+$", message = "project.location.invalid")
	private String longitude;
	
	@NotNull(message = "polygon.empty")
	private List<Coordinates> polygonCoordinates;
	
	@NotNull(message = "project.area.required")
	@DecimalMin(value = "0.1",inclusive = false,message = "project.area.min")
	@DecimalMax(value = "20",message = "project.area.max")
	private Double area;

	private Double[] offsetPoint;
}
