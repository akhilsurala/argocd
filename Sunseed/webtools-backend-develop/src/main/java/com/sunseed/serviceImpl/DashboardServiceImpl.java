package com.sunseed.serviceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sunseed.entity.Projects;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.ResourceNotFoundException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.responseDTO.DashBoardResponseDto;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.DashboardService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

//	private final SimulatedRunRepository simulatedRunRepository;
	private final UserProfileRepository userProfileRepository;

	@Override
	public DashBoardResponseDto getDashboardDetails(Long userId) {

		UserProfile userProfile = userProfileRepository.findByUserId(userId)
				.orElseThrow(() -> new ResourceNotFoundException("user.not.found"));

		List<Long> projectIds = userProfile.getUserProjects().stream().map(Projects::getProjectId)
				.collect(Collectors.toList());

		if (projectIds == null) {
			throw new UnprocessableException("user.dontHave.projects");
		}

//		List<SimulatedRun> simulatedRuns = simulatedRunRepository.findByProject_ProjectIdIn(projectIds);
//
//		if (simulatedRuns == null) {
//			throw new UnprocessableException("simulatedRuns.not.found");
//		}

		DashBoardResponseDto response = new DashBoardResponseDto();

		response.setProjectCount(projectIds.size());
//		response.setSimulatedRuns(simulatedRuns.size());
		return response;
	}

}
