package com.sunseed.simtool.service;

import java.io.IOException;
import java.util.Map;

import com.sunseed.simtool.model.response.SimulationResponseDto;

public interface SimulationBlockwiseService {

	SimulationResponseDto createSimulationBlockwise(Map<String, Object> runPayload, Long projectId, Long userProfileId)
			throws NumberFormatException, IOException;

}
