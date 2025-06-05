package com.sunseed.helper;

import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.Projects;
import com.sunseed.entity.UserProfile;
import com.sunseed.exceptions.ProjectsException;
import com.sunseed.model.Coordinates;
import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;

public class ProjectsHelper {

	public static ProjectsCreationResponseDto projectsToProjectCreationResponse(Projects project) {

		ProjectsCreationResponseDto responseDto = ProjectsCreationResponseDto.builder().latitude(project.getLatitude())
				.longitude(project.getLongitude()).projectId(project.getProjectId())
				.polygonCoordinates(convertJsonStringToArray(project.getPolygonCoordinates()))
				.projectName(project.getProjectName()).projectStatus(project.getProjectStatus().getValue()).offSetPoint(project.getOffsetPoint()).build();

		return responseDto;
	}

	public static Projects projectCreationRequestToProjects(ProjectsCreationRequestDto requestDto,
			UserProfile userProfile) {

		Projects project = Projects.builder().projectName(requestDto.getProjectName())
				.latitude(requestDto.getLatitude()).longitude(requestDto.getLongitude())
				.polygonCoordinates(convertPolygonCoordinatesToJson(requestDto.getPolygonCoordinates()))
				.area(requestDto.getArea()).userProfile(userProfile).offsetPoint(requestDto.getOffsetPoint()).build();
		return project;
	}

	public static Projects projectUpdationRequestToProjects(Projects existingProject,
			ProjectsUpdationRequestDto requestDto) {

		existingProject.setProjectName(requestDto.getProjectName());
		existingProject.setLatitude(requestDto.getLatitude());
		existingProject.setLongitude(requestDto.getLongitude());
		existingProject.setPolygonCoordinates(convertPolygonCoordinatesToJson(requestDto.getPolygonCoordinates()));
		existingProject.setArea(requestDto.getArea());
		existingProject.setComments(requestDto.getComments());
		existingProject.setOffsetPoint(requestDto.getOffsetPoint());
		return existingProject;
	}

	public static ProjectsUpdationResponseDto projectsToProjectsUpdationResponse(Projects project) {

		ProjectsUpdationResponseDto responseDto = ProjectsUpdationResponseDto.builder()
				.projectId(project.getProjectId()).projectName(project.getProjectName()).latitude(project.getLatitude())
				.longitude(project.getLongitude())
				.polygonCoordinates(convertJsonStringToArray(project.getPolygonCoordinates())).runIds(project.getRunIds())
				.comments(project.getComments()).projectStatus(project.getProjectStatus().getValue()).offSetPoint(project.getOffsetPoint()). build();
		return responseDto;
	}

	private static String convertPolygonCoordinatesToJson(List<Coordinates> polygonCoordinates) {

		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.setSerializationInclusion(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL);
		String coordinatesJsonString;
		try {
			coordinatesJsonString = objectMapper.writeValueAsString(polygonCoordinates);
		} catch (JsonProcessingException e) {

			throw new ProjectsException(null, "polygon.parsing.error", HttpStatus.UNPROCESSABLE_ENTITY);
		}
		return coordinatesJsonString;
	}

	private static Coordinates[] convertJsonStringToArray(Object polygonCoordinates) {

		ObjectMapper objectMapper = new ObjectMapper();
		Coordinates[] coordinatesArray = null;
		try {
			String jsonString=null;
			if(polygonCoordinates instanceof String)
				jsonString = (String) polygonCoordinates;
			if(jsonString!=null)
			coordinatesArray = objectMapper.readValue(jsonString, Coordinates[].class);
		} catch (JsonProcessingException e) {
			throw new ProjectsException(null, "polygon.parsing.error", HttpStatus.UNPROCESSABLE_ENTITY);
		}

		return coordinatesArray;
	}
}
