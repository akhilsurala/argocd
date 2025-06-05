package com.sunseed.simtool.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sunseed.simtool.config.E2EServerConfigProperties;
import com.sunseed.simtool.config.SimulationServer;
import com.sunseed.simtool.constant.SceneType;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.exception.NoSuitableNodeFoundException;
import com.sunseed.simtool.model.BaseServer;
import com.sunseed.simtool.model.E2EServer;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.service.E2EService;
import com.sunseed.simtool.service.MachineProvisionService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class E2EServiceImpl implements E2EService {

	private final E2EMachineNodeRepository e2eMachineNodeRepository;
	private final MachineProvisionService machineProvisionService;
	private final List<SimulationServer> simulationServers;

	private final E2EServerConfigProperties e2EServerConfigProperties;

	@Value("${e2e.maxNumberOfNodes:1}")
	private int maxNumberOfNodes;

	@Value("${e2e.gpuConfigValue:1}")
	private int gpuConfigForE2ENode;

	@Value("${ssh.key.path}")
	private String sshKeyPath;

	@Value("${e2e.free-memory}")
	private Double freeMemory;

	@Value("${e2e.free-cpu}")
	private Integer freeCpu;

	private final Lock lock = new ReentrantLock();

	@Override
	@Transactional
	public BaseServer getRunnableServer(SimulationTask task, SceneType sceneType) {
		if (maxNumberOfNodes < 1) {
			log.debug("Max number of nodes is not given, falling back to local server. for taskId: {}", task.getId());
			return populateLocalServer(task.getCpuRequired(), sceneType);
		}

		lock.lock();
		try {
			// checking running nodes
			long runningNodesCount = e2eMachineNodeRepository.countByStatusIn(List.of("running"));

			if (runningNodesCount == 0) {
				return createNewNodeIfAllowed(task);
			}

			boolean nodeExists = e2eMachineNodeRepository.existsNodeWithSufficientResources(
					task.getCpuRequired() + freeCpu, task.getRamRequired() + freeMemory);

			if (!nodeExists) {
				throw new NoSuitableNodeFoundException("No capable node for taskId: " + task.getId());
			}

			// 1. Try to find a running node with available capacity
			Optional<E2EMachineNode> optionalRunningNode = e2eMachineNodeRepository
					.findBestFitNode(task.getCpuRequired(), task.getRamRequired(), freeCpu, freeMemory, "Running");

			if (optionalRunningNode.isPresent()) {
				E2EMachineNode presentMachineNode = optionalRunningNode.get();
				BigDecimal loadRequired = calculateLoadRequired(task.getCpuRequired(), freeCpu,
						presentMachineNode.getVcpus(), task.getRamRequired(), freeMemory,
						presentMachineNode.getMemory());
				E2EMachineNode updatedNode = updateE2EMachineNode(task.getId(), loadRequired,
						optionalRunningNode.get());
				if (updatedNode != null) {
					log.debug("Assigned existing running node: {} to taskId: {}", updatedNode.getNodeId(),
							task.getId());
					return populateE2EServer(task, loadRequired, updatedNode, sceneType);
				}
			}

			return createNewNodeIfAllowed(task);
		} finally {
			lock.unlock();
		}
	}

	private BaseServer createNewNodeIfAllowed(SimulationTask task) {

		// Check if any node is currently in "Creating" state
		boolean creatingNodeExists = e2eMachineNodeRepository.existsByStatus("Creating");
		if (creatingNodeExists) {
			return null; // Node is still being created, return null to retry later
		}

		// Check the count of active nodes including running, creating nodes
		List<String> statuses = List.of("running", "creating");
		long activeNodesCount = e2eMachineNodeRepository.countByStatusIn(statuses);

		if (activeNodesCount < maxNumberOfNodes) {
			log.debug("Creating a new node... for taskId: {}", task.getId());
			E2EMachineNode newNode = createE2EMachineNode(gpuConfigForE2ENode > 0 ? gpuConfigForE2ENode : 1);
			if (newNode != null) {
				log.debug("New node created: {}. Returning to retry assignment later. for taskId: {}",
						newNode.getNodeId(), task.getId());
			}
			return null; // Return null so the task can retry later when the new node becomes available
		}

		// If no node is allowed to be created and no nodes available, return null
		return null;
	}

	private E2EMachineNode createE2EMachineNode(int gpuConfigForE2ENode) {

		Optional<E2EMachineNode> optionalApplicableNode = machineProvisionService
				.provisionMachine(e2EServerConfigProperties.getServerConfigList(), gpuConfigForE2ENode);
		if (optionalApplicableNode.isEmpty())
			return null;
		E2EMachineNode applicableNode = optionalApplicableNode.get();
		return applicableNode;
	}

	private E2EMachineNode updateE2EMachineNode(Long taskId, BigDecimal loadRequired, E2EMachineNode e2eMachineNode) {
		BigDecimal updatedLoad = e2eMachineNode.getCurrentLoad().add(loadRequired).setScale(2, RoundingMode.HALF_UP);
		if (updatedLoad.compareTo(BigDecimal.valueOf(100.0).setScale(2, RoundingMode.HALF_UP)) <= 0) {
			e2eMachineNode.setCurrentLoad(updatedLoad.setScale(2, RoundingMode.HALF_UP));
			log.debug("Updating load of nodeId : {} with load : {} for taskId: {}", e2eMachineNode.getNodeId(),
					loadRequired, taskId);
			return e2eMachineNodeRepository.save(e2eMachineNode);
		} else
			return null;
	}

	private BaseServer populateE2EServer(SimulationTask task, BigDecimal load, E2EMachineNode e2eMachineNode,
			SceneType sceneType) {
		return new E2EServer(e2eMachineNode.getOs().getOs_name(), sceneType.toString(), task.getCpuRequired(), load,
				e2eMachineNode.getUsername(), e2eMachineNode.getPassword(), e2eMachineNode.getPublic_ip_address(),
				sshKeyPath, e2eMachineNode.getNodeId());
	}

	private BaseServer populateLocalServer(Integer cpuRequired, SceneType sceneType) {
		SimulationServer simulationServer = simulationServers.stream()
				.filter(server -> sceneType.toString().equalsIgnoreCase(server.getType())
						&& server.getCpu().availablePermits() >= cpuRequired)
				.findFirst().orElse(null);
		if (simulationServer == null)
			return null;
		return simulationServer;
	}

	private BigDecimal calculateLoadRequired(double taskCpu, double freeCpu, int vcpus, double taskMemory,
			double freeMemory, String memoryStr) {
		// Extract numeric memory from string like "110 GB"
		double totalMemory;
		try {
			totalMemory = Double.parseDouble(memoryStr.replaceAll("(?i)\\s*GB", "").trim());
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Invalid memory format: " + memoryStr);
		}

		// Avoid division by zero
		double cpuDenominator = vcpus - freeCpu;
		double memoryDenominator = totalMemory - freeMemory;

		double cpuLoad = taskCpu / cpuDenominator;
		double memoryLoad = taskMemory / memoryDenominator;

		double load = 100 * Math.max(cpuLoad, memoryLoad);
		return roundToTwoDecimals(load);
	}

	private BigDecimal roundToTwoDecimals(double value) {
		return BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP);
	}

}