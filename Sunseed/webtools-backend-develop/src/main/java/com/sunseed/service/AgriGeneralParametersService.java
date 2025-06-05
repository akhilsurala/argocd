package com.sunseed.service;

import java.util.Map;

import com.sunseed.model.requestDTO.AgriGeneralParametersRequestDto;

public interface AgriGeneralParametersService {

	Map<String, Object> addAgriGeneralParameters(AgriGeneralParametersRequestDto request, Long projectId, Long runId,
			Long userId);

	Map<String, Object> updateAgriGeneralParameters(AgriGeneralParametersRequestDto request, Long projectId,
			Long agriGeneralParameterId, Long runId, Long userId);

	Map<String, Object> getAgriGeneralParameters(Long projectId, Long runId, Long userId);
	
	Map<String, Object> getMasterData();

}
