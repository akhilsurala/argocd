package com.sunseed.service;

import java.util.Map;

import com.sunseed.entity.Runs;
import com.sunseed.model.requestDTO.PvParametersRequestDto;

public interface RunCloneService {

	Map<String, Object> getPvParametersWithToggle(Long userId, Long projectId, Long runId);

	Map<String, Object> createCloneForGivenRun(PvParametersRequestDto request, Long projectId, String toggle,
			Long runId, Long userId, Boolean isMaster);

	Map<String, Object> getAllVariantRuns(Long projectId, Long runId, Long userId);

	Runs updateRunMasterStatus(Long projectId, Long runId, Long userId);

}
