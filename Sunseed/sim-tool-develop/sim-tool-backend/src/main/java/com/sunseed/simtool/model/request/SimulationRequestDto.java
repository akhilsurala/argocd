package com.sunseed.simtool.model.request;

import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimulationRequestDto {

	@NotNull(message = "User Profile Id can't be null")
	private Long userProfileId;
	@NotNull(message = "Project Id can't be null")
	private Long projectId;
	
	private List<Map<String, Object>> runPayload;
}
