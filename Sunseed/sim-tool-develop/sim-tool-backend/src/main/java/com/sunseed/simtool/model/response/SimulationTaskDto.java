package com.sunseed.simtool.model.response;

import java.time.LocalDateTime;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SimulationTaskDto {

	private LocalDateTime dateTime;
	private Float pvYield;
	private Float frontGain;
	private Float rearGain;
	private Float albedo;
	private Map<String, Double> carbonAssimilation;
	private Float temperature;
}
