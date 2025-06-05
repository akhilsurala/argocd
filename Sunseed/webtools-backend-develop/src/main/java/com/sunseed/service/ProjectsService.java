package com.sunseed.service;

import java.util.List;

import com.sunseed.entity.Projects;
import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
import com.sunseed.model.responseDTO.ProjectsListResponseDto;
import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;

public interface ProjectsService {

	ProjectsCreationResponseDto createProject(ProjectsCreationRequestDto requestDto, Long userId);

	ProjectsUpdationResponseDto updateProject(ProjectsUpdationRequestDto requestDto, Long projectId);

	List<ProjectsListResponseDto> getAllProjects(Long userId,String searchText);

	ProjectsUpdationResponseDto getUserProjectUsingProjectId(Long userId, Long projectId);
	ProjectsUpdationResponseDto deleteProject(Long projectId,Long userId);

}
