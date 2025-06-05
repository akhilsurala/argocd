package com.sunseed.service;

import com.sunseed.model.requestDTO.EconomicParametersRequestDto;
import com.sunseed.model.responseDTO.EconomicParametersResponseDto;

public interface EconomicParametersService {
    EconomicParametersResponseDto createEconomicParameters(EconomicParametersRequestDto request, Long projectId,Long userId, Long runId);

    EconomicParametersResponseDto getEconomicParameters(Long projectId, Long userId,Long runId);

    EconomicParametersResponseDto updateEconomicParameters(Long projectId, EconomicParametersRequestDto request, Long economicParameterId,Long userId, Long runId);
}
