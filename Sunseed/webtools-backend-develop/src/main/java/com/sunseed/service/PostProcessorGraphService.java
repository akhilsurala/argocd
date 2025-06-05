package com.sunseed.service;

import java.util.HashMap;
import java.util.List;

public interface PostProcessorGraphService {
	
	HashMap<String, Object> getAllSimulationFromRunId(Long projectId, Long runId, Long userId);

	HashMap<String, Object> getAllCropData(Long projectId, List<Long> runId, Long userId, List<Long> cropId, String typeGraph);

}
