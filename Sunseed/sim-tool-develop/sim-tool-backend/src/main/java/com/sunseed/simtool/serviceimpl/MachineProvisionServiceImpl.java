package com.sunseed.simtool.serviceimpl;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.sunseed.simtool.embeddables.MachineSpecifications;
import com.sunseed.simtool.embeddables.OSInfo;
import com.sunseed.simtool.entity.E2EMachineNode;
import com.sunseed.simtool.entity.E2EMachineSpecifications;
import com.sunseed.simtool.model.E2EServerConfig;
import com.sunseed.simtool.model.request.NodeRequest;
import com.sunseed.simtool.model.response.GpuMachineSpecificationResponse;
import com.sunseed.simtool.model.response.NodeCreateResponse;
import com.sunseed.simtool.model.response.NodeCreateResponse.NodeCreateResult;
import com.sunseed.simtool.repository.E2EMachineNodeRepository;
import com.sunseed.simtool.repository.E2EMachineSpecificationsRepository;
import com.sunseed.simtool.service.MachineProvisionService;
import com.sunseed.simtool.service.StatusMonitorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MachineProvisionServiceImpl implements MachineProvisionService {

	private final WebClient webClient;
	private final E2EMachineSpecificationsRepository e2eMachineSpecificationsRepository;
	private final E2EMachineNodeRepository e2eMachineNodeRepository;
	private final StatusMonitorService statusMonitorService;

	@Value("${e2e.api.base-url}")
	private String E2E_BASE_URL;

	@Value("#{'${ssh.key}'.split(',')}")
	private List<String> sshKeys;

	private static final Pattern GPU_PATTERN = Pattern.compile("-(\\d+)x");

	@Override
	public Optional<E2EMachineNode> provisionMachine(List<E2EServerConfig> properties, int requiredGPUCount) {

		for (E2EServerConfig property : properties) {
			List<GpuMachineSpecificationResponse.GpuMachineData> gpuOptions = fetchGpuOptions(property);

			if (gpuOptions.isEmpty())
				continue;

			List<GpuMachineSpecificationResponse.GpuMachineData> filteredGpuResponses = filterGpuResponses(gpuOptions,
					requiredGPUCount);

			if (filteredGpuResponses.isEmpty())
				continue;

			for (GpuMachineSpecificationResponse.GpuMachineData gpuResponse : filteredGpuResponses) {
				Optional<E2EMachineSpecifications> optionalInstance = findOrCreateMachineInstance(gpuResponse);

				if (optionalInstance.isEmpty())
					continue;

				E2EMachineSpecifications instance = optionalInstance.get();

				E2EMachineNode node = tryCreateMachineViaAPI(instance, property);

				if (node == null)
					continue;

				// now saving the instance and node
				instance = e2eMachineSpecificationsRepository.save(instance);
				node.setSpecifications(instance);
				node = e2eMachineNodeRepository.save(node);
				statusMonitorService.scheduleStatusCheck(property, node.getPlan(), node.getNodeId());
				return Optional.of(node);
			}
		}
		return Optional.empty();
	}

	private E2EMachineNode tryCreateMachineViaAPI(E2EMachineSpecifications instance, E2EServerConfig property) {

		NodeRequest nodeRequest = createNodeRequestBody(instance, property);
		NodeCreateResponse nodeCreateResponse = getCreateNodeResponse(property, nodeRequest);
		if (nodeCreateResponse.getCode() != 200)
			return null;
		NodeCreateResponse.NodeCreateResult nodeData = nodeCreateResponse.getData().getNode_create_response().get(0);
		return populateE2EMachineNode(property, nodeData);
	}

	private E2EMachineNode populateE2EMachineNode(E2EServerConfig property, NodeCreateResult result) {
		E2EMachineNode node = new E2EMachineNode();

		node.setNodeId(result.getId());
		node.setNodeName(result.getName());
		node.setUsername(property.getUsername());
		node.setPassword(property.getPassword());
		node.setVm_id(result.getVm_id());
		node.setNodeCreatedAtE2E(result.getCreated_at());
		node.setPublic_ip_address(result.getPublic_ip_address());
		node.setPrivate_ip_address(result.getPrivate_ip_address());
		node.setBackup(result.isBackup());
		node.setDisk(result.getDisk());
		node.setStatus(result.getStatus());
		node.setVcpus(result.getVcpus()); // assuming your setter also sets capacity
		node.setMemory(result.getMemory());
		node.setPlan(result.getPlan());
		node.setRegion(result.getRegion());
		node.set_locked(result.is_locked());
		node.setGpu(result.getGpu());
		node.setPrice(result.getPrice());
		node.setAdditional_ip(result.getAdditional_ip());
		node.setLabel(result.getLabel());
		node.set_active(result.is_active());
		node.setScaler_id(result.getScaler_id());
		node.setOs(mapToOsInfo(result.getOs_info()));
		node.set_monitored(result.is_monitored());
		node.setBackup_status(mapToBackupStatus(result.getBackup_status()));
		node.setLocation(result.getLocation());
		node.setMonitor_status(mapToMonitorStatus(result.getMonitor_status()));
		node.setEnable_bitninja_details(mapToBitNinjaDetails(result.getEnable_bitninja_details()));
		node.set_bitninja_license_active(result.is_bitninja_license_active());
		node.setAny_license_attached(mapToLicenseInfo(result.getAny_license_attached()));
		node.set_committed(result.is_committed());
		node.setAudit_log_message(result.getAudit_log_message());
		node.setMonitoring_tab_enabled(result.isMonitoring_tab_enabled());
		node.setCdp_tab_enabled(result.isCdp_tab_enabled());
		node.setAlert_tab_enabled(result.isAlert_tab_enabled());
		node.setBitninja_discount_percentage(result.getBitninja_discount_percentage());
		node.set_image_deleted(result.is_image_deleted());
		node.setVpc_enabled(result.isVpc_enabled());
		node.set_snapshot_allowed(result.is_snapshot_allowed());
		node.set_fortigate_vm(result.is_fortigate_vm());
		node.setRescue_mode_status(result.getRescue_mode_status());
		node.set_upgradable(result.is_upgradable());
		node.setAbuse_flag(result.isAbuse_flag());
		node.setCurrency(result.getCurrency());
		node.setVm_type(result.getVm_type());
		node.set_accidental_protection(result.is_accidental_protection());
		node.setProject_name(result.getProject_name());
		node.setResource_type(result.getResource_type());
		node.setLabel_id(result.getLabel_id());

		List<com.sunseed.simtool.embeddables.SSHKey> sshKeyList = result.getSsh_keys().stream().map(this::mapToSSHKey)
				.collect(Collectors.toList());

		node.setSsh_keys(sshKeyList);

		return node;
	}

	private OSInfo mapToOsInfo(NodeCreateResponse.NodeCreateResult.OsInfo source) {
		if (source == null)
			return null;

		OSInfo target = new OSInfo();
		target.setOs_name(source.getName());
		target.setOs_version(source.getVersion());
		target.setOs_category(source.getCategory());
		target.setOs_image(source.getFull_name()); // assuming full_name maps to os_image

		return target;
	}

	private com.sunseed.simtool.embeddables.BackupStatus mapToBackupStatus(
			NodeCreateResponse.NodeCreateResult.BackupStatus source) {
		if (source == null)
			return null;

		com.sunseed.simtool.embeddables.BackupStatus target = new com.sunseed.simtool.embeddables.BackupStatus();
		target.setBackupStatus(source.getStatus());
		target.setBackupDetail(source.getDetail());
		target.setBackupNodeId(source.getNode_id());
		target.setBackup_is_encryption_enabled(source.is_encryption_enabled());

		return target;
	}

	private com.sunseed.simtool.embeddables.MonitorStatus mapToMonitorStatus(
			NodeCreateResponse.NodeCreateResult.MonitorStatus source) {
		if (source == null)
			return null;

		com.sunseed.simtool.embeddables.MonitorStatus target = new com.sunseed.simtool.embeddables.MonitorStatus();
		target.setMonitorStatus(source.getStatus());
		target.setMonitorReason(source.getReason());

		return target;
	}

	private com.sunseed.simtool.embeddables.BitNinjaDetails mapToBitNinjaDetails(
			NodeCreateResponse.NodeCreateResult.EnableBitninjaDetails source) {
		if (source == null)
			return null;

		com.sunseed.simtool.embeddables.BitNinjaDetails target = new com.sunseed.simtool.embeddables.BitNinjaDetails();
		target.setShow_bitninja(source.isShow_bitninja());
		target.setBitninja_cost(source.getBitninja_cost());

		return target;
	}

	private com.sunseed.simtool.embeddables.LicenseInfo mapToLicenseInfo(
			NodeCreateResponse.NodeCreateResult.LicenseStatus source) {
		if (source == null)
			return null;

		com.sunseed.simtool.embeddables.LicenseInfo target = new com.sunseed.simtool.embeddables.LicenseInfo();
		target.set_license_attached(source.is_license_attached());
		target.setLicense_deletion_message(source.getLicense_deletion_message());
		target.setMssql_license_attached(source.isMssql_license_attached());

		return target;
	}

	private com.sunseed.simtool.embeddables.SSHKey mapToSSHKey(NodeCreateResponse.NodeCreateResult.SshKey source) {
		if (source == null)
			return null;

		com.sunseed.simtool.embeddables.SSHKey target = new com.sunseed.simtool.embeddables.SSHKey();
		target.setLabel(source.getLabel());
		target.setSsh_key(source.getSsh_key());

		return target;
	}

	private NodeCreateResponse getCreateNodeResponse(E2EServerConfig property, NodeRequest request) {
		URI uri = UriComponentsBuilder.fromHttpUrl(E2E_BASE_URL + "nodes/")
				.queryParam("location", property.getLocation()).queryParam("apikey", property.getApiKey()).build()
				.toUri();

		try {
			return webClient.post().uri(uri).headers(headers -> headers.setBearerAuth(property.getAuthToken()))
					.contentType(MediaType.APPLICATION_JSON).body(BodyInserters.fromValue(request)).retrieve()
					.onStatus(HttpStatusCode::is4xxClientError,
							clientResponse -> clientResponse.bodyToMono(String.class).map(errorBody -> {
								log.debug("Client error response: {}" + errorBody);
								return new RuntimeException("Client Error: " + errorBody);
							}))
					.onStatus(HttpStatusCode::is5xxServerError,
							clientResponse -> clientResponse.bodyToMono(String.class).map(errorBody -> {
								log.debug("Server error response: {}" + errorBody);
								return new RuntimeException("Server Error: " + errorBody);
							}))
					.bodyToMono(NodeCreateResponse.class).block();
		} catch (Exception ex) {
			log.debug("Exception occurred while calling E2E: {}" + ex.getMessage());
			ex.printStackTrace();
			return null;
		}
	}

	private NodeRequest createNodeRequestBody(E2EMachineSpecifications instance, E2EServerConfig property) {
		NodeRequest nodeRequest = NodeRequest.builder().label("default")
				.name(instance.getMachineName().toLowerCase().replaceAll("[^a-z0-9\\-]", "-"))
				.region(instance.getRegion()).plan(instance.getPlan()).image(instance.getImage())
				.ssh_keys(sshKeys.stream().map(String::trim).collect(Collectors.toList())).start_scripts(List.of())
				.backups(false).enable_bitninja(false).disable_password(true).is_saved_image(true)
				.saved_image_template_id(property.getSavedImageTemplateId()).reserve_ip("").is_ipv6_availed(false)
				.default_public_ip(false).ngc_container_id(null).number_of_instances(1).security_group_id(42802)
				.build();
		return nodeRequest;
	}

	private Optional<E2EMachineSpecifications> findOrCreateMachineInstance(
			GpuMachineSpecificationResponse.GpuMachineData gpuResponse) {

		String planId = gpuResponse.getPlan();
		Optional<E2EMachineSpecifications> existing = e2eMachineSpecificationsRepository.findByPlan(planId);
		return existing.isPresent() ? existing : Optional.of(convertToE2EMachineSpec(gpuResponse));
	}

	public E2EMachineSpecifications convertToE2EMachineSpec(GpuMachineSpecificationResponse.GpuMachineData source) {
		E2EMachineSpecifications target = new E2EMachineSpecifications();

		target.setMachineName("Sunseed-APV-"+source.getName());
		target.setPlan(source.getPlan());
		target.setImage(source.getImage());
		target.setLocation(source.getLocation());
		target.setRegion(source.getLocation()); // Assuming region = location for now

		// Set OS info
		if (source.getOs() != null) {
			OSInfo osInfo = new OSInfo();
			osInfo.setOs_name(source.getOs().getName());
			osInfo.setOs_version(source.getOs().getVersion());
			osInfo.setOs_image(source.getOs().getImage());
			osInfo.setOs_category(source.getOs().getCategory());
			target.setOs(osInfo);
		}

		// Set machine specs
		if (source.getSpecs() != null) {
			MachineSpecifications specs = new MachineSpecifications();
			specs.setCPU(source.getSpecs().getCpu());
			specs.setRAM(Double.parseDouble(source.getSpecs().getRam())); // assuming it's a string like "128"
			specs.setDisk_space(source.getSpecs().getDiskSpace());
			specs.setPrice_per_month(source.getSpecs().getPricePerMonth());
			specs.setPrice_per_hour(source.getSpecs().getPricePerHour());
			target.setMachineSpecs(specs);
		}

		// Set GPU card details
		if (source.getGpuCardDetails() != null) {
			com.sunseed.simtool.embeddables.GpuCardDetails gpu = new com.sunseed.simtool.embeddables.GpuCardDetails();
			gpu.setCARD_NAME(source.getGpuCardDetails().getCardName());
			try {
				gpu.setTEMPLATE_ID(Long.parseLong(source.getGpuCardDetails().getTemplateId()));
			} catch (NumberFormatException e) {
				gpu.setTEMPLATE_ID(0); // or handle default
			}
			gpu.setMEMORY_UNIT(source.getGpuCardDetails().getMemoryUnit());
			try {
				gpu.setMEMORY(Long.parseLong(source.getGpuCardDetails().getMemory()));
			} catch (NumberFormatException e) {
				gpu.setMEMORY(0); // or handle default
			}
			gpu.setCARD_TYPE(source.getGpuCardDetails().getCardType());
			target.setGpuCardDetails(gpu);
		}

		return target;
	}

	private List<GpuMachineSpecificationResponse.GpuMachineData> filterGpuResponses(
			List<GpuMachineSpecificationResponse.GpuMachineData> gpuOptions, int requiredGPUCount) {

		return gpuOptions.stream().filter(GpuMachineSpecificationResponse.GpuMachineData::isAvailableInventoryStatus)
				.filter(r -> {
					String cardName = Optional.ofNullable(r.getGpuCardDetails())
							.map(GpuMachineSpecificationResponse.GpuCardDetails::getCardName).orElse("");
					Matcher matcher = GPU_PATTERN.matcher(cardName);
					return matcher.find() && Integer.parseInt(matcher.group(1)) == requiredGPUCount;
				}).toList();
	}

	private List<GpuMachineSpecificationResponse.GpuMachineData> fetchGpuOptions(E2EServerConfig property) {

		URI uri = UriComponentsBuilder.fromHttpUrl(E2E_BASE_URL + "images/")
				.queryParam("gpu_type", property.getGpuType()).queryParam("os", property.getOs())
				.queryParam("osversion", property.getOsVersion()).queryParam("category", property.getCategory())
				.queryParam("apikey", property.getApiKey()).queryParam("project_id", property.getProjectId())
				.queryParam("location", property.getLocation()).build().toUri();

		String response = webClient.get().uri(uri).headers(headers -> headers.setBearerAuth(property.getAuthToken()))
				.retrieve().bodyToMono(String.class).block();

		if (response == null)
			return Collections.emptyList();

		try {
			ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
					.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			GpuMachineSpecificationResponse parsed = mapper.readValue(response, GpuMachineSpecificationResponse.class);
			return parsed.getData() != null ? parsed.getData() : Collections.emptyList();

		} catch (Exception e) {
			e.printStackTrace();
			return Collections.emptyList();
		}
	}
}
