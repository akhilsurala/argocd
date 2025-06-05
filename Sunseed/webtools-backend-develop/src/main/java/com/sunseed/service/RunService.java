package com.sunseed.service;

import java.util.List;
import java.util.Map;

import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Runs;
import com.sunseed.model.responseDTO.SimulationResponseDto;
import com.sunseed.model.responseDTO.SimulationTaskStatusDto;

public interface RunService {

    Runs createRun(Long projectId, PreProcessorToggle toggle);

    Runs updateRun(Long runId, PreProcessorToggle toggles);

    Map<String, Object> getAllRuns(Long projectId, String bay, Long userId, Long runId, String searchText);

    Runs getRunById(Long runId);

    List<SimulationResponseDto> postSimulation(List<Long> runId, Long projectId, Long userId);

    Map<String, List<SimulationTaskStatusDto>> updateSimulatedRunStatus(String status, Long runId);

    void deleteRun(Long userId, Long projectId, Long runId);

    Map<String, Object> updateAgriPvControlStatusOfRun(Long projectId, Long runId, Long userId, Boolean agriControl,
                                                       Boolean pvControl);

    Map<String, Object> getRunNames(Long projectId, Long userId, List<Long> runIdList);

    com.sunseed.projection.SceneResponse getAllScenesForRun(Long projectId, Long runId, Long userId);

}
