package com.sunseed.service;

import java.util.Map;

import com.sunseed.model.requestDTO.PvParametersRequestDto;

public interface PvParametersService {
	
	Map<String, Object> addOrUpdatePvParametersWithToggle(PvParametersRequestDto request, Long projectId, String toggle,
			Long runId, Long userId, String callFor, Long pvParameterId);

	Map<String, Object> getPvParametersWithToggle(Long userId, Long projectId, Long runId);

	Map<String, Object> getMasterData(String mode);

}
