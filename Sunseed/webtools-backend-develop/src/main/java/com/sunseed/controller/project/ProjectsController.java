package com.sunseed.controller.project;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
import com.sunseed.model.responseDTO.ProjectsListResponseDto;
import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.ProjectsService;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class ProjectsController {

    private final ProjectsService projectsService;
    private final ApiResponse apiResponse;

    @PostMapping("/project")
    public ResponseEntity<Object> createProject(@Valid @RequestBody ProjectsCreationRequestDto requestDto,
                                                HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        ProjectsCreationResponseDto responseDto = projectsService.createProject(requestDto, userId);
        return apiResponse.commonResponseHandler(responseDto, "project.created", HttpStatus.CREATED);
    }

    @PutMapping("/project/{projectId}")
    public ResponseEntity<Object> updateProject(@Valid @RequestBody ProjectsUpdationRequestDto requestDto,
                                                @PathVariable("projectId") Long projectId) {

        ProjectsUpdationResponseDto responseDto = projectsService.updateProject(requestDto, projectId);
        return apiResponse.commonResponseHandler(responseDto, "project.updated", HttpStatus.OK);
    }

    @GetMapping("/projects")
    public ResponseEntity<Object> getAllProjects(@Nullable @RequestParam("searchText") String searchText,
                                                 HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        List<ProjectsListResponseDto> responseDto = projectsService.getAllProjects(userId, searchText);
        return apiResponse.commonResponseHandler(responseDto, "project.fetched.forUser", HttpStatus.OK);
    }

    @GetMapping("/projects/{projectId}")
    public ResponseEntity<Object> getUserProjectUsingProjectId(HttpServletRequest request,
                                                @PathVariable("projectId") Long projectId) {

        Long userId = (Long) request.getAttribute("userId");
        ProjectsUpdationResponseDto responseDto = projectsService.getUserProjectUsingProjectId(userId, projectId);
        return apiResponse.commonResponseHandler(responseDto, "project.fetched.forUser", HttpStatus.OK);
    }

    //********** soft delete *****************
    @DeleteMapping("/project/{projectId}")
    public ResponseEntity<Object> deleteProject(@PathVariable("projectId") Long projectId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        ProjectsUpdationResponseDto responseDto = projectsService.deleteProject(projectId, userId);
        return apiResponse.commonResponseHandler(responseDto, "project.deleted", HttpStatus.OK);
    }

}
