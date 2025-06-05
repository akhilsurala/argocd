package com.sunseed.serviceImpl;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sunseed.entity.Projects;
import com.sunseed.entity.UserProfile;
import com.sunseed.enums.CommonStatus;
import com.sunseed.exceptions.ProjectsException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.ProjectsHelper;
import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
import com.sunseed.model.responseDTO.ProjectsListResponseDto;
import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.UserProfileRepository;
import com.sunseed.service.ProjectsService;

import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectsServiceImpl implements ProjectsService {

    private final UserProfileRepository userProfileRepository;
    private final ProjectsRepository projectsRepository;

    @Override
    public ProjectsCreationResponseDto createProject(ProjectsCreationRequestDto requestDto, Long userId) {

        if (userId == null || userId <= 0)
            throw new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND);

        Optional<UserProfile> userProfile = userProfileRepository.findByUserId(userId);

        if (userProfile.isEmpty())
            throw new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND);

        Optional<Projects> existingProject = projectsRepository
                .existsByUserProfileAndProjectName(userProfile.get().getUserProfileId(), requestDto.getProjectName());

        if (existingProject.isPresent()) {

            ProjectsCreationResponseDto responseDto = ProjectsHelper
                    .projectsToProjectCreationResponse(existingProject.get());
            throw new ProjectsException(responseDto, "project.already.exists", HttpStatus.CONFLICT);
        }

        // project creation
        Projects newProject = ProjectsHelper.projectCreationRequestToProjects(requestDto, userProfile.get());
        Projects savedProject = projectsRepository.save(newProject);
        ProjectsCreationResponseDto responseDto = ProjectsHelper.projectsToProjectCreationResponse(savedProject);
        return responseDto;
    }

    @Override
    public ProjectsUpdationResponseDto updateProject(ProjectsUpdationRequestDto requestDto, Long projectId) {

        if (projectId == null || projectId <= 0)
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);

        Optional<Projects> existingProject = projectsRepository.findById(projectId);

        if (existingProject.isEmpty())
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);

        if (existingProject.get().getProjectStatus() == CommonStatus.INACTIVE)
            throw new ProjectsException(null, "project.inactive", HttpStatus.UNPROCESSABLE_ENTITY);

        Projects foundProject = existingProject.get();
        Long userProfileId = foundProject.getUserProfile().getUserProfileId();

        Long existingProjectId = projectsRepository.findIdWithUserProfileAndProjectNameIgnoreCase(userProfileId,
                requestDto.getProjectName());
        if (existingProjectId != null && existingProjectId != projectId)
            throw new UnprocessableException("same.project");
        if (requestDto.getComments() != null)
            foundProject.setComments(requestDto.getComments());
        foundProject.setProjectName(requestDto.getProjectName());

        if (requestDto.getRunIds() != null && requestDto.getRunIds().size() > 0) {
            System.out.println("enter in runIds condition");
            foundProject.setRunIds(requestDto.getRunIds());

        }
        System.out.println("*********** before update project *********  ");
        Projects updatedProject = projectsRepository.saveAndFlush(foundProject);
        ProjectsUpdationResponseDto responseDto = ProjectsHelper.projectsToProjectsUpdationResponse(updatedProject);
        return responseDto;
    }

    @Override
    public List<ProjectsListResponseDto> getAllProjects(Long userId, String searchText) {

        if (userId == null || userId <= 0)
            throw new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND);

        UserProfile userProfile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND));

        List<Tuple> results;
        if (searchText == null || searchText.isBlank()) {
            results = projectsRepository.findProjectsWithRunCountByUserProfileId(userProfile.getUserProfileId());
        } else {
            results = projectsRepository
                    .findProjectsWithRunCountByUserProfileIdAndSearchText(userProfile.getUserProfileId(), searchText);
        }


        // new field added
        List<ProjectsListResponseDto> dtos = results.stream().map(tuple -> {
            // Retrieve runIds as a JSON string
            String runIdsJson = tuple.get("runIds", String.class);
            List<Long> runIds = new ArrayList<>();

            // Parse JSON string to List<Long> if not null
            if (runIdsJson != null) {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    runIds = objectMapper.readValue(runIdsJson, new TypeReference<List<Long>>() {
                    });
                } catch (JsonProcessingException e) {
                    e.printStackTrace(); // Handle parsing error as needed
                }
            }

            return ProjectsListResponseDto.builder()
                    .projectId(tuple.get("projectId", Long.class))
                    .projectName(tuple.get("projectName", String.class))
                    .latitude(tuple.get("latitude", String.class))
                    .longitude(tuple.get("longitude", String.class))
                    .createdOn(tuple.get("createdOn", Instant.class))
                    .lastEdited(tuple.get("lastEdited", Instant.class))
                    .numberOfRuns(tuple.get("numberOfRuns", Long.class))
                    .comments(tuple.get("comments", String.class))
                    .location(tuple.get("location", String.class))
                    .offsetPoint(tuple.get("offsetPoint", Double[].class))
                    .runIds(runIds)  // Set parsed runIds
                    .build();
        }).collect(Collectors.toList());

        return dtos;

    }

    @Override
    public ProjectsUpdationResponseDto getUserProjectUsingProjectId(Long userId, Long projectId) {
        if (projectId == null || projectId <= 0)
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);

        Optional<Projects> existingProject = projectsRepository.findById(projectId);

        if (existingProject.isEmpty())
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);

        if (existingProject.get().getProjectStatus() == CommonStatus.INACTIVE)
            throw new ProjectsException(null, "project.inactive", HttpStatus.UNPROCESSABLE_ENTITY);

        Projects foundProject = existingProject.get();
        Long userProfileId = foundProject.getUserProfile().getUserProfileId();

        Long existingProjectId = projectsRepository.existsByUserProfileAndProjectId(userProfileId, projectId);
        if (existingProjectId != null && existingProjectId != projectId)
            throw new UnprocessableException("same.project");

        Optional<Projects> projectOptional = projectsRepository.findById(projectId);

        if (projectOptional.isEmpty())
            return null;
        

        ProjectsUpdationResponseDto responseDto = ProjectsHelper.projectsToProjectsUpdationResponse(projectOptional.get());
        return responseDto;
        // return project.get();
    }

    @Override
    public ProjectsUpdationResponseDto deleteProject(Long projectId, Long userId) {
        if (projectId == null || projectId <= 0)
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);
        if (userId == null || userId <= 0)
            throw new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND);

        Optional<UserProfile> userProfile = userProfileRepository.findByUserId(userId);

        if (userProfile.isEmpty())
            throw new ProjectsException(null, "user.not.found", HttpStatus.NOT_FOUND);


        Optional<Projects> existingProject = projectsRepository.findById(projectId);

        if (existingProject.isEmpty())
            throw new ProjectsException(null, "project.not.found", HttpStatus.NOT_FOUND);
        if (!existingProject.get().getUserProfile().getUserProfileId().equals(userProfile.get().getUserProfileId())) {
            throw new ProjectsException(null, "project.mismatch", HttpStatus.FORBIDDEN);
        }
        Projects project = existingProject.get();
        project.setProjectStatus(CommonStatus.INACTIVE);
        Projects projects = projectsRepository.saveAndFlush(project);
        ProjectsUpdationResponseDto responseDto = ProjectsHelper.projectsToProjectsUpdationResponse(projects);
        return responseDto;
    }


}
