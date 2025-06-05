package com.sunseed.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunseed.model.requestDTO.HourlyDetailsPayload;
import com.sunseed.model.responseDTO.PostProcessingDetailsResponseDto;

import java.util.List;
import java.util.Map;

public interface PostprocessingDetailsService {

    List<?> getHourlyDetails(HourlyDetailsPayload request, String quantity, String dataType, String frequency, Long projectId, Long userId) throws Exception;

    Map<String, List<PostProcessingDetailsResponseDto>> getPostprocessingDetails(List<Long> runIds, Long userId, Long projectId, String dataType, String frequency);
}
