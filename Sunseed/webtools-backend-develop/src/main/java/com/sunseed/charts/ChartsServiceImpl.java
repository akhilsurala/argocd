package com.sunseed.charts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.Projects;
import com.sunseed.enums.RunStatus;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnAuthorizedException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.mappers.AgriGeneralModelMapper;
import com.sunseed.mappers.CropParameterModelMapper;
import com.sunseed.mappers.EconomicParameterModelMapper;
import com.sunseed.mappers.PvParameterModelMapper;
import com.sunseed.model.responseDTO.RunDesignExplorerResponseDto;
import com.sunseed.projection.RunDesignExplorerProjection;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.RunsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChartsServiceImpl implements ChartsService {

	private final ProjectsRepository projectsRepository;
	private final RunsRepository runsRepository;
	private final PvParameterModelMapper pvParameterModelMapper;
	private final CropParameterModelMapper cropParameterModelMapper;
	private final EconomicParameterModelMapper economicParameterModelMapper;
	private final AgriGeneralModelMapper agriGeneralModelMapper;

	@Override
	public Map<String, Object> getAllRunsInOutDataForDesignExplorer(Long projectId, Long userId, List<Long> runIdList) {

		if (userId == null || userId <= 0)
			throw new UnAuthorizedException(null, "user.not.found");

		Projects project = projectsRepository.findById(projectId)
				.orElseThrow(() -> new ResourceNotFoundException("project.not.found"));

		Long existingUserId = project.getUserProfile().getUserId();

		if (userId != existingUserId) {
			throw new UnAuthorizedException("user.not.authorized");
		}

		// now checking for null list or any null entry in list
		if (runIdList.isEmpty() || runIdList.stream().anyMatch(Objects::isNull))
			throw new UnprocessableException("runIdList.cant.be.empty");

		// now getting run details from runIdList info
		List<RunDesignExplorerProjection> existingRunDetailsList = runsRepository
				.getRunDetailsListForDesignExplorer(projectId, RunStatus.COMPLETED, runIdList);

		// checking if run details list is empty that means either not found or are not
		// of a single group
		if (existingRunDetailsList.isEmpty())
			throw new UnprocessableException("runIdList.not.of.same.group");

		// now setting up response
		Map<String, Object> response = new HashMap<>();
		List<RunDesignExplorerResponseDto> runDetailsList = new ArrayList<>();

		for (RunDesignExplorerProjection currentRun : existingRunDetailsList) {

			RunDesignExplorerResponseDto runResponseDto = new RunDesignExplorerResponseDto();

			long runId = currentRun.id();
			runResponseDto.setId(currentRun.id());
			runResponseDto.setRunName(currentRun.runName());
			runResponseDto.setProjectId(currentRun.projectId());
			runResponseDto.setRunStatus(currentRun.runStatus().getValue());
			runResponseDto.setCreatedAt(currentRun.createdAt());
			runResponseDto.setUpdatedAt(currentRun.updatedAt());
			runResponseDto.setCloneId(currentRun.cloneId());
			runResponseDto.setIsMaster(currentRun.isMaster());
			runResponseDto.setAgriControl(currentRun.agriControl());
			runResponseDto.setPvControl(currentRun.pvControl());

			if (currentRun.simulatedRun() != null)
				runResponseDto.setSimulatedId(currentRun.simulatedRun().getSimulatedId());

			PreProcessorToggle toggles = currentRun.preProcessorToggle();
			runResponseDto.setPreProcessorToggle(toggles);

			if (currentRun.pvParameters() != null) {
				runResponseDto.setPvParameters(pvParameterModelMapper
						.entityToPvParameterResponseDto(currentRun.pvParameters(), toggles, runId));
			}

			if (currentRun.cropParameters() != null) {
				runResponseDto.setCropParameters(cropParameterModelMapper
						.entityToCropParametersResponseDto(currentRun.cropParameters(), runResponseDto.getId()));
			}

			if (currentRun.agriGeneralParameters() != null) {
				runResponseDto.setAgriGeneralParameters(agriGeneralModelMapper
						.entityToAgriGeneralParameterResponseDto(currentRun.agriGeneralParameters()));
			}

			if (currentRun.economicParameters() != null) {
				runResponseDto.setEconomicParameters(
						economicParameterModelMapper.getEconomicParameterResponseDto(currentRun.economicParameters()));
			}
			runDetailsList.add(runResponseDto);
		}

		response.put("runs", runDetailsList);
		return response;
	}

}
