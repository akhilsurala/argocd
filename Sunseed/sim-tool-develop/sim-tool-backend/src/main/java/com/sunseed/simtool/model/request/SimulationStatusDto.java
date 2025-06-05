package com.sunseed.simtool.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SimulationStatusDto {

	@NotNull(message = "Simulation id can't be null")
	private Long simulationId;
	@Pattern(regexp = "Cancel|Pause|Resume", flags = Pattern.Flag.CASE_INSENSITIVE, message = "Must be cancel, pause or resume")
	private String status; //Cancel, Pause, Resume
}
