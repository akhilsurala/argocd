package com.sunseed.charts;

import java.util.List;
import java.util.Map;

public interface ChartsService {

	Map<String,Object> getAllRunsInOutDataForDesignExplorer(Long projectId, Long userId,
			List<Long> runIdList);

}
