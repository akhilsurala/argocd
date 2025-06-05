package com.sunseed.simtool.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.sunseed.simtool.service.NodeCleanupService;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NodeScheduledCleanupTask {

	private NodeCleanupService nodeCleanupService;

	@Autowired
	public void ScheduledCleanupTask(NodeCleanupService nodeCleanupService) {
		this.nodeCleanupService = nodeCleanupService;
	}

	@Scheduled(initialDelay = 5 * 60 * 1000,fixedRate = 10 * 60 * 1000) // every 10 mins
	public void scheduledNodeCleanup() {
		log.debug("Node cleanup started from scheduledNodeCleanup() method");
		nodeCleanupService.triggerCleanup();
	}
}
