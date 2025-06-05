package com.sunseed.simtool.serviceimpl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import com.sunseed.simtool.config.SimulationServer;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.model.BaseServer;
import com.sunseed.simtool.model.E2EServer;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.service.NodeCleanupService;
import com.sunseed.simtool.service.ResourceReleaserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResourceReleaserServiceImpl implements ResourceReleaserService {

	private final E2EMachineNodeRepository e2eMachineNodeRepository;
	private final NodeCleanupService nodeCleanupService;

	@Override
	@Transactional
	public void finallyReleasingServerResources(BaseServer baseServer, SimulationTask task) {
		log.info("Finally releasing resources for taskId: {}", task.getId());

		if (baseServer instanceof SimulationServer simulationServer) {
			simulationServer.getCpu().release(task.getCpuRequired());
		} else if (baseServer instanceof E2EServer e2eServer) {
			Long nodeId = e2eServer.getNodeId();
			Optional<E2EMachineNode> optionalNode = e2eMachineNodeRepository.findByNodeIdForUpdate(nodeId);
			if (optionalNode.isEmpty())
				return;
			E2EMachineNode node = optionalNode.get();
			if (node.getCurrentLoad().compareTo(((E2EServer) baseServer).getLoad()) >= 0) {
				node.setCurrentLoad(node.getCurrentLoad().subtract(((E2EServer) baseServer).getLoad()));
				e2eMachineNodeRepository.save(node);
				log.info("Reduced load: {} for nodeId: {} by taskId: {}", ((E2EServer) baseServer).getLoad(), nodeId,
						task.getId() != null ? task.getId() : "");
			} else {
				log.warn("Load was already 0 for nodeId: {}", nodeId);
			}

			// Register cleanup to happen AFTER commit
			// triggering for cleanup
			TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
				@Override
				public void afterCommit() {
					log.debug("Node cleanup started by finallyReleasingServerResources() method by taskId: {}",
							task.getId() != null ? task.getId() : "");
					nodeCleanupService.triggerCleanup();
				}
			});
		}
	}
}
