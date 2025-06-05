package com.sunseed.simtool.serviceimpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.sunseed.simtool.model.E2EServerConfig;
import com.sunseed.simtool.model.response.PlanStatusResponse;
import com.sunseed.simtool.model.response.PlanStatusResponse.PlanStatusData;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.service.StatusMonitorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatusMonitorServiceImpl implements StatusMonitorService {

	private final E2EMachineNodeRepository e2eMachineNodeRepository;

	private final WebClient webClient = WebClient.builder()
			.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).build();

	private final ConcurrentMap<Long, Future<?>> activeTasks = new ConcurrentHashMap<>();
	private final ExecutorService executorService = Executors.newCachedThreadPool();

	@Value("${e2e.api.base-url}")
	private String E2E_BASE_URL;

	@Value("${status-monitor.interval-seconds}")
	private int intervalSeconds;

	@Value("${status-monitor.timeout-minutes}")
	private int timeoutMinutes;

	@Override
	public void scheduleStatusCheck(E2EServerConfig property, String planId, Long machineNodeId) {
		if (activeTasks.containsKey(machineNodeId)) {
			log.info("Status monitoring already active for nodeId: {}", machineNodeId);
			return;
		}

		log.info("Starting status monitoring for planId: {} (machineNodeId: {})", planId, machineNodeId);

		Future<?> future = executorService.submit(() -> {
			long startTime = System.currentTimeMillis();
			while ((System.currentTimeMillis() - startTime) < timeoutMinutes * 60_000L) {
				try {
					boolean shouldStop = checkPlanStatus(property, planId, machineNodeId);
					if (shouldStop)
						break;
					Thread.sleep(intervalSeconds * 1000);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				} catch (Exception e) {
					log.error("Error checking status for nodeId {}: {}", machineNodeId, e.getMessage(), e);
				}
			}

			if ((System.currentTimeMillis() - startTime) >= timeoutMinutes * 60_000) {
				log.warn("Timeout reached for nodeId: {} after {} minutes. Deleting node...", machineNodeId,
						timeoutMinutes);
				handleFailedNode(property, machineNodeId);
			}

			activeTasks.remove(machineNodeId);
		});

		activeTasks.put(machineNodeId, future);
	}

	private boolean checkPlanStatus(E2EServerConfig property, String planId, Long machineNodeId) {
		URI uri = UriComponentsBuilder.fromHttpUrl(E2E_BASE_URL + "nodes/" + machineNodeId)
				.queryParam("apikey", property.getApiKey()).queryParam("location", property.getLocation()).build()
				.toUri();

		try {
			PlanStatusResponse response = webClient.get().uri(uri).headers(headers -> {
				headers.setBearerAuth(property.getAuthToken());
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
			}).retrieve().bodyToMono(PlanStatusResponse.class).block(Duration.ofSeconds(30));

			if (response != null && response.getData() != null && !response.getData().isEmpty()) {
				Optional<PlanStatusData> matchingPlanOpt = response.getData().stream()
						.filter(node -> node.getId() == machineNodeId.longValue()).findFirst();

				if (matchingPlanOpt.isPresent()) {
					String status = matchingPlanOpt.get().getStatus();

					if ("Running".equalsIgnoreCase(status)) {
						try {
							log.info("NodeId:{} status is found to be running", machineNodeId);
							String currentStatus = e2eMachineNodeRepository.findByNodeId(machineNodeId)
									.map(node -> node.getStatus()).orElse("");
							if (!"Running".equalsIgnoreCase(currentStatus)) {
								Thread.sleep(2 * 60 * 1000L); // delay 2 minutes
								updateMachineNodeStatus(machineNodeId, "Running");
							}
							return true;
						} catch (Exception e) {
							log.warn("Exception while updating node status: {}", e);
							return true;
						}
					} else if ("Failed".equalsIgnoreCase(status) || "Deleted".equalsIgnoreCase(status)
							|| "Terminating".equalsIgnoreCase(status)) {
						log.debug("NodeId:{} status found to be:{}. Deleting the node",machineNodeId,status);
						updateMachineNodeStatusDeletedAndReduceLoad(machineNodeId);
						return true;
					}
				} else {
					log.warn("NodeId {} not found in response. Assuming node is deleted.", machineNodeId);
					updateMachineNodeStatusDeletedAndReduceLoad(machineNodeId);
					return true;
				}
			} else {
				log.warn("Empty response for nodeId {}. Assuming node is deleted.", machineNodeId);
				updateMachineNodeStatusDeletedAndReduceLoad(machineNodeId);
				return true;
			}
		} catch (Exception e) {
			log.error("Exception during GET node status check for nodeId {}: {}", machineNodeId, e.getMessage(), e);
		}

		return false;
	}

	private void updateMachineNodeStatus(Long nodeId, String newStatus) {
		e2eMachineNodeRepository.findByNodeId(nodeId).ifPresent(node -> {
			log.info("Updating node {} status to {}", nodeId, newStatus);
			node.setStatus(newStatus);
			e2eMachineNodeRepository.save(node);
		});
	}

	private void updateMachineNodeStatusDeletedAndReduceLoad(Long nodeId) {
		e2eMachineNodeRepository.findByNodeId(nodeId).ifPresent(node -> {
			log.info("Updating node {} status to Deleted", nodeId);
			node.setStatus("Deleted");
			node.setCurrentLoad(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
			e2eMachineNodeRepository.save(node);
		});
	}

	@Override
	public void handleFailedNode(E2EServerConfig property, Long machineNodeId) {
		URI uri = UriComponentsBuilder.fromHttpUrl(E2E_BASE_URL + "nodes/" + machineNodeId + "/")
				.queryParam("apikey", property.getApiKey()).queryParam("location", property.getLocation()).build()
				.toUri();

		int maxRetries = 5;
		log.warn("Initiating deletion for machineNodeId {} with maxRetries: {}", machineNodeId, maxRetries);

		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				ResponseEntity<String> response = webClient.delete().uri(uri).headers(headers -> {
					headers.setBearerAuth(property.getAuthToken());
					headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
				}).retrieve().toEntity(String.class).block(Duration.ofSeconds(30));

				if (response != null && response.getStatusCode().is2xxSuccessful()) {
					log.info("Node {} deleted successfully (response: {})", machineNodeId, response.getBody());
					updateMachineNodeStatusDeletedAndReduceLoad(machineNodeId);
					break;
				} else {
					log.warn("Attempt {}: Failed to delete node {}. Status: {}", attempt, machineNodeId,
							response != null ? response.getStatusCode() : "null");
				}
			} catch (Exception e) {
				log.error("Attempt {}: Exception while deleting node {}: {}", attempt, machineNodeId, e.getMessage(),
						e);
			}

			try {
				Thread.sleep(intervalSeconds * 1000L);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}
	}
}
