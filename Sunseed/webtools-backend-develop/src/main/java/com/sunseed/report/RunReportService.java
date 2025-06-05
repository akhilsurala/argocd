package com.sunseed.report;

import java.util.List;
import java.util.Map;

public interface RunReportService {

	Map<String, Object> getRunReport(Long projectId, List<Long> runIds, Long userId);

}
