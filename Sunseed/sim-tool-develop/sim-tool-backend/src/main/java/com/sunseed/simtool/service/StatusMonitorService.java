package com.sunseed.simtool.service;

import com.sunseed.simtool.model.E2EServerConfig;

public interface StatusMonitorService {

	public void scheduleStatusCheck(E2EServerConfig property, String planId, Long machineNodeId);
	public void handleFailedNode(E2EServerConfig property, Long machineNodeId);
}
