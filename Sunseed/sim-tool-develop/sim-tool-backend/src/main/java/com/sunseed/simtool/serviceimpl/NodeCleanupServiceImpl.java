package com.sunseed.simtool.serviceimpl;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sunseed.simtool.config.E2EServerConfigProperties;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.model.E2EServerConfig;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.service.NodeCleanupService;
import com.sunseed.simtool.service.RabbitMQService;
import com.sunseed.simtool.service.StatusMonitorService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NodeCleanupServiceImpl implements NodeCleanupService {

	private E2EMachineNodeRepository e2eMachineNodeRepository;
	private ExecutorService cleanupExecutor;
	private E2EServerConfigProperties e2EServerConfigProperties;
	private StatusMonitorService statusMonitorService;

	private final ConcurrentHashMap<Long, Boolean> activeCleanupTasks = new ConcurrentHashMap<>();

	@Value("${rabbitmq.queue.agri}")
	private String agriQueueName;

	@Autowired
	public void NodeCleanupService(E2EMachineNodeRepository e2eMachineNodeRepository, RabbitMQService rabbitMQService,
			@Qualifier("nodeCleanupExecutor") ExecutorService cleanupExecutor,
			E2EServerConfigProperties e2EServerConfigProperties, StatusMonitorService statusMonitorService) {
		this.e2eMachineNodeRepository = e2eMachineNodeRepository;
		this.cleanupExecutor = cleanupExecutor;
		this.e2EServerConfigProperties = e2EServerConfigProperties;
		this.statusMonitorService = statusMonitorService;
	}

	@Override
	public void triggerCleanup() {
		cleanupExecutor.submit(this::cleanupNodes);
	}

	@Override
	public void cleanupNodes() {
		List<E2EMachineNode> idleNodes = e2eMachineNodeRepository.findRunningNodesWithZeroLoad();

		if (idleNodes.isEmpty()) {
			log.debug("No node found with empty load");
			return;
		}

		for (int i = 0; i < idleNodes.size(); i++) {
			E2EMachineNode node = idleNodes.get(i);

			// Check if cleanup for this node is already in progress
			if (activeCleanupTasks.putIfAbsent(node.getNodeId(), Boolean.TRUE) != null) {
				log.info("Cleanup already in progress for nodeId: {}", node.getNodeId());
				continue; // Skip this node as cleanup is already in progress
			}

			try {
				// Now starting cleaning up the node
				E2EServerConfig property = findMatchingConfig(node.getUsername(), node.getPassword());
				log.debug("Deleting node starts from cleanupNodes() method for nodeId: {}", node.getNodeId());

				// Wait for 5 minutes before checking load again
				Thread.sleep(5 * 60 * 1000);

				// Re-fetching and checking load again
				E2EMachineNode latestNode = e2eMachineNodeRepository.findById(node.getId()).orElse(null);
				if (latestNode != null && latestNode.getCurrentLoad().compareTo(BigDecimal.ZERO) == 0) {
					statusMonitorService.handleFailedNode(property, latestNode.getNodeId());
					log.info("Deleted idle node after confirming 0 load.");
				} else {
					log.info("Skipped deletion of idle node as load is now non-zero.");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				log.error("Cleanup process interrupted for nodeId: {}", node.getNodeId());
			} catch (Exception e) {
				log.error("Error occurred while cleaning up nodeId: {}", node.getNodeId(), e);
			} finally {
				activeCleanupTasks.remove(node.getNodeId());
			}
		}
	}

	private E2EServerConfig findMatchingConfig(String username, String password) {
		return e2EServerConfigProperties.getServerConfigList().stream()
				.filter(cfg -> cfg.getUsername().equals(username) && cfg.getPassword().equals(password)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("No matching config found"));
	}
}
