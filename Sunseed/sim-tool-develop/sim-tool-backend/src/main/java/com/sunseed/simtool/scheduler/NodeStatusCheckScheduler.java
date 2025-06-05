package com.sunseed.simtool.scheduler;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.sunseed.simtool.config.E2EServerConfigProperties;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.model.E2EServerConfig;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.service.StatusMonitorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NodeStatusCheckScheduler {

	private final StatusMonitorService statusMonitorService;
	private final E2EMachineNodeRepository e2eMachineNodeRepository;
	private final E2EServerConfigProperties e2EServerConfigProperties;

	@Scheduled(fixedRate = 15 * 60 * 1000) // Every 15 minutes
	public void scheduledNodeStatusCheck() {

		log.info("Starting scheduled node status checks...");

		// Fetch all nodes that need a status check (e.g., "Creating" or "Failed" or "Running" nodes)
		List<E2EMachineNode> nodes = getNodesToCheck();
		if (nodes.isEmpty()) {
			log.info("No nodes to check at this time.");
			return;
		}

		// Loop through all the nodes and schedule a status check for each
		for (E2EMachineNode node : nodes) {

			E2EServerConfig property = findMatchingConfig(node.getUsername(), node.getPassword());
			if (property == null) {
				log.warn("No matching config found for nodeId: {}", node.getNodeId());
				continue;
			}
			log.debug("Starting status check from scheduledNodeStatusCheck for nodeId: {}", node.getNodeId());
			try {
				statusMonitorService.scheduleStatusCheck(property, node.getPlan(), node.getNodeId());
			} catch (Exception e) {
				log.error("Failed to schedule status check for nodeId: {}", node.getNodeId(), e);
			}
		}
	}

	private List<E2EMachineNode> getNodesToCheck() {
		return e2eMachineNodeRepository.findAllByStatusIn(List.of("creating","running"));
	}

	private E2EServerConfig findMatchingConfig(String username, String password) {
		return e2EServerConfigProperties.getServerConfigList().stream()
				.filter(cfg -> cfg.getUsername().equals(username) && cfg.getPassword().equals(password)).findFirst()
				.orElse(null);
	}
}
