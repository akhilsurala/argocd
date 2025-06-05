package com.sunseed.service;

import com.sunseed.model.requestDTO.SimulationRequest;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;

import java.util.List;
import java.util.Map;

public interface SimulationService {

    List<SimulationResponseDto> postSimulation(List<Long> runId, Long projectId);
    Map<String, List<SimulationTaskStatusDto>> updateStatus(String status, Long simulatedId);
}
