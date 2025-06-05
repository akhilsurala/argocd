package com.sunseed.service;

import com.sunseed.model.requestDTO.CropParameterRequestDto;
import com.sunseed.model.responseDTO.CropParametersResponseDto;

import jakarta.validation.Valid;

public interface CropParameterService {

	CropParametersResponseDto saveCropParameter(CropParameterRequestDto request, Long projectId,Long runId, Long userId);

	CropParametersResponseDto getCropParameters(Long projectId, Long userId, Long runId);

	CropParametersResponseDto updateCropParameter(@Valid CropParameterRequestDto request, Long projectId, Long cropParameterId,Long runId, Long userId);

}
