//package com.sunseed.serviceImpl;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//
//import java.time.Instant;
//import java.util.List;
//import java.util.Optional;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.sunseed.entity.Projects;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.CommonStatus;
//import com.sunseed.exceptions.ProjectsException;
//import com.sunseed.model.Coordinates;
//import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
//import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
//import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
//import com.sunseed.model.responseDTO.ProjectsListResponseDto;
//import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;
//import com.sunseed.repository.ProjectsRepository;
//import com.sunseed.repository.UserProfileRepository;
//
//@ExtendWith(MockitoExtension.class)
//public class ProjectsServiceImplTests {
//
//	@Mock
//	private UserProfileRepository userProfileRepository;
//	@Mock
//	private ProjectsRepository projectsRepository;
//
//	@InjectMocks
//	private ProjectsServiceImpl projectsServiceImpl;
//
//	/*
//	 * Test cases for createProject() method
//	 */
//
//	@DisplayName("test for createProject when userId is null")
//	@Test
//	public void createProject_whenUserIdIsNull() {
//
//		// given
//		Long userId = null;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.createProject(requestDto, userId);
//		});
//	}
//
//	@DisplayName("test for createProject when userId is negative")
//	@Test
//	public void createProject_whenUserIdIsNegative() {
//
//		// given
//		Long userId = -1L;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.createProject(requestDto, userId);
//		});
//	}
//
//	@DisplayName("test for createProject when userId is zero")
//	@Test
//	public void createProject_whenUserIdIsZero() {
//
//		// given
//		Long userId = 0L;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.createProject(requestDto, userId);
//		});
//	}
//
//	@DisplayName("test for createProject when user does not exists")
//	@Test
//	public void createProject_whenUserDoesNotExists() {
//
//		// given
//		Long userId = 1L;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.empty());
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.createProject(requestDto, userId);
//		});
//	}
//
//	@DisplayName("test for createProject when project already exists")
//	@Test
//	public void createProject_whenProjectAlreadyExists() {
//
//		// given
//		Long userId = 1L;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		UserProfile userProfile = UserProfile.builder().createdAt(Instant.now()).emailId("test@gmail.com")
//				.firstName("test").updatedAt(Instant.now()).userId(1L).userProfileId(1L).userProjects(null).build();
//
//		Projects project = Projects.builder().projectId(1L).projectStatus(CommonStatus.ACTIVE).area(0.5)
//				.createdAt(Instant.now()).updatedAt(Instant.now()).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectName("MyProject").userProfile(userProfile).build();
//
//		userProfile.setUserProjects(List.of(project));
//
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(projectsRepository.existsByUserProfileAndProjectName(eq(1L), eq("MyProject")))
//				.willReturn(Optional.of(project));
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.createProject(requestDto, userId);
//		});
//	}
//
//	@DisplayName("test for createProject when project does not exists")
//	@Test
//	public void createProject_whenProjectDoesNotExists() {
//
//		// given
//		Long userId = 1L;
//		ProjectsCreationRequestDto requestDto = ProjectsCreationRequestDto.builder().projectName("MyProject")
//				.latitude("123.123").longitude("789.789").polygonCoordinates(List.of()).area(0.5).build();
//
//		UserProfile userProfile = UserProfile.builder().createdAt(Instant.now()).emailId("test@gmail.com")
//				.firstName("test").updatedAt(Instant.now()).userId(1L).userProfileId(1L).userProjects(null).build();
//
//		Projects project = Projects.builder().projectId(1L).projectStatus(CommonStatus.ACTIVE).area(0.5)
//				.createdAt(Instant.now()).updatedAt(Instant.now()).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectName("MyProject").userProfile(userProfile).build();
//
//		userProfile.setUserProjects(List.of(project));
//
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(projectsRepository.existsByUserProfileAndProjectName(eq(1L), eq("MyProject")))
//				.willReturn(Optional.empty());
//		given(projectsRepository.save(any())).willReturn(project);
//
//		// when
//		ProjectsCreationResponseDto response = projectsServiceImpl.createProject(requestDto, userId);
//
//		// then
//		assertThat(response).isNotNull();
//		assertThat(response.getProjectId()).isEqualTo(1L);
//		assertThat(response.getProjectName()).isEqualTo("MyProject");
//		assertThat(response.getProjectStatus()).isEqualTo(CommonStatus.ACTIVE.getValue());
//		assertThat(response.getLatitude()).isEqualTo("123.123");
//		assertThat(response.getLongitude()).isEqualTo("789.789");
//		assertThat(response.getPolygonCoordinates()).isEqualTo(new Coordinates[0]);
//	}
//
//	/*
//	 * Test cases for updateProject() method
//	 */
//
//	@DisplayName("Test case for updateProject() when projectId is null")
//	@Test
//	public void updateProject_whenProjectIdIsNull() {
//
//		// given
//		Long projectId = null;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject")
//				.comments("This is a test project").build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.updateProject(requestDto, projectId);
//		});
//	}
//
//	@DisplayName("Test case for updateProject() when projectId is negative")
//	@Test
//	public void updateProject_whenProjectIdIsNegative() {
//
//		// given
//		Long projectId = -1L;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject")
//				.comments("This is a test project").build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.updateProject(requestDto, projectId);
//		});
//	}
//
//	@DisplayName("Test case for updateProject() when projectId is zero")
//	@Test
//	public void updateProject_whenProjectIdIsZero() {
//
//		// given
//		Long projectId = 0L;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject")
//				.comments("This is a test project").build();
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.updateProject(requestDto, projectId);
//		});
//	}
//
//	@DisplayName("Test case for updateProject() when project is not present")
//	@Test
//	public void updateProject_whenProjectIsNotPresent() {
//
//		// given
//		Long projectId = 1L;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject")
//				.comments("This is a test project").build();
//
//		given(projectsRepository.findById(eq(1L))).willReturn(Optional.empty());
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.updateProject(requestDto, projectId);
//		});
//	}
//
//	@DisplayName("Test case for updateProject() when project is inactive")
//	@Test
//	public void updateProject_whenProjectIsInActive() {
//
//		// given
//		Long projectId = 1L;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject")
//				.comments("This is a test project").build();
//
//		Projects project = Projects.builder().area(0.5).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectId(1L).projectName("MyProject").projectStatus(CommonStatus.INACTIVE)
//				.build();
//
//		given(projectsRepository.findById(eq(1L))).willReturn(Optional.of(project));
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.updateProject(requestDto, projectId);
//		});
//	}
//
//	@DisplayName("Test case for updateProject() for updating project")
//	@Test
//	public void updateProject_forUpdatingProject() {
//
//		// given
//		Long projectId = 1L;
//		ProjectsUpdationRequestDto requestDto = ProjectsUpdationRequestDto.builder().projectName("MyProject1")
//				.comments("This is a test project").build();
//
//		Projects project = Projects.builder().area(0.5).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectId(1L).projectName("MyProject").projectStatus(CommonStatus.ACTIVE)
//				.build();
//
//		Projects updatedProject = Projects.builder().area(0.5).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectId(1L).projectName("MyProject1").comments("This is a test project")
//				.projectStatus(CommonStatus.ACTIVE).build();
//
//		given(projectsRepository.findById(eq(1L))).willReturn(Optional.of(project));
//		given(projectsRepository.save(any())).willReturn(updatedProject);
//
//		// then
//		ProjectsUpdationResponseDto response = projectsServiceImpl.updateProject(requestDto, projectId);
//		assertThat(response).isNotNull();
//		assertThat(response.getComments()).isEqualTo("This is a test project");
//		assertThat(response.getLatitude()).isEqualTo("123.123");
//		assertThat(response.getLongitude()).isEqualTo("789.789");
//		assertThat(response.getPolygonCoordinates()).isEqualTo(new Coordinates[0]);
//		assertThat(response.getProjectId()).isEqualTo(1L);
//		assertThat(response.getProjectName()).isEqualTo("MyProject1");
//		assertThat(response.getProjectStatus()).isEqualTo(CommonStatus.ACTIVE.getValue());
//	}
//
//	/*
//	 * Test cases for getAllProjects() method
//	 */
//
//	@DisplayName("Test case for getAllProjects() when user id is null")
//	@Test
//	public void getAllProjects_whenUserIdIsNull() {
//
//		// given
//		Long userId = null;
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.getAllProjects(userId);
//		});
//	}
//
//	@DisplayName("Test case for getAllProjects() when user id is negative")
//	@Test
//	public void getAllProjects_whenUserIdIsNegative() {
//
//		// given
//		Long userId = -1L;
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.getAllProjects(userId);
//		});
//	}
//
//	@DisplayName("Test case for getAllProjects() when user id is zero")
//	@Test
//	public void getAllProjects_whenUserIdIsZero() {
//
//		// given
//		Long userId = 0L;
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.getAllProjects(userId);
//		});
//	}
//
//	@DisplayName("Test case for getAllProjects() when user does not exists")
//	@Test
//	public void getAllProjects_whenUserDoesNotExists() {
//
//		// given
//		Long userId = 1L;
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.empty());
//
//		// then
//		assertThrows(ProjectsException.class, () -> {
//			projectsServiceImpl.getAllProjects(userId);
//		});
//	}
//
//	@DisplayName("Test case for getAllProjects() getting all user projects")
//	@Test
//	public void getAllProjects() {
//
//		// given
//		Long userId = 1L;
//		UserProfile userProfile = UserProfile.builder().createdAt(Instant.now()).emailId("test@gmail.com")
//				.firstName("test").updatedAt(Instant.now()).userId(1L).userProfileId(1L).userProjects(null).build();
//
//		Projects project = Projects.builder().projectId(1L).projectStatus(CommonStatus.ACTIVE).area(0.5)
//				.createdAt(Instant.now()).updatedAt(Instant.now()).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectName("MyProject").userProfile(userProfile).build();
//
//		userProfile.setUserProjects(List.of(project));
//
//		given(userProfileRepository.findByUserId(eq(1L))).willReturn(Optional.of(userProfile));
//		given(projectsRepository.findByUserProfileUserProfileId(eq(1L))).willReturn(List.of(project));
//		given(projectsRepository.countRunsByProjectId(eq(1L))).willReturn(1L);
//
//		// then
//		List<ProjectsListResponseDto> listResponse = projectsServiceImpl.getAllProjects(userId);
//		assertThat(listResponse).isNotNull();
//		assertThat(listResponse.size()).isEqualTo(1);
//		assertThat(listResponse.get(0).getProjectId()).isEqualTo(1L);
//		assertThat(listResponse.get(0).getProjectName()).isEqualTo("MyProject");
//		assertThat(listResponse.get(0).getNumberOfRuns()).isEqualTo(1L);
//	}
//
//
//	/*
//	 * Test cases for getUserProjectUsingProjectId() method
//	 */
//
//	@DisplayName("Test case for getUserProjectUsingProjectId() when project id is null")
//	@Test
//	public void getUserProjectUsingProjectId_whenProjectIdIsNull() {
//
//		// given
//		Long projectId = null;
//
//		// then
//		Projects project = projectsServiceImpl.getUserProjectUsingProjectId(projectId);
//		assertNull(project);
//	}
//
//	@DisplayName("Test case for getUserProjectUsingProjectId() when project id is negative")
//	@Test
//	public void getUserProjectUsingProjectId_whenProjectIdIsNegative() {
//
//		// given
//		Long projectId = -1L;
//
//		// then
//		Projects project = projectsServiceImpl.getUserProjectUsingProjectId(projectId);
//		assertNull(project);
//	}
//
//	@DisplayName("Test case for getUserProjectUsingProjectId() when project id is zero")
//	@Test
//	public void getUserProjectUsingProjectId_whenProjectIdIsZero() {
//
//		// given
//		Long projectId = 0L;
//
//		// then
//		Projects project = projectsServiceImpl.getUserProjectUsingProjectId(projectId);
//		assertNull(project);
//	}
//
//	@DisplayName("Test case for getUserProjectUsingProjectId() when project not found")
//	@Test
//	public void getUserProjectUsingProjectId_whenProjectNotFound() {
//
//		// given
//		Long projectId = 1L;
//		given(projectsRepository.findById(eq(1L))).willReturn(Optional.empty());
//
//		// then
//		Projects project = projectsServiceImpl.getUserProjectUsingProjectId(projectId);
//		assertNull(project);
//	}
//
//	@DisplayName("Test case for getUserProjectUsingProjectId() get project")
//	@Test
//	public void getUserProjectUsingProjectId_getProject() {
//
//		// given
//		Long projectId = 1L;
//		Projects project = Projects.builder().area(0.5).latitude("123.123").longitude("789.789")
//				.polygonCoordinates("[]").projectId(1L).projectName("MyProject").projectStatus(CommonStatus.ACTIVE)
//				.build();
//		given(projectsRepository.findById(eq(1L))).willReturn(Optional.of(project));
//
//		// then
//		Projects response = projectsServiceImpl.getUserProjectUsingProjectId(projectId);
//		assertThat(response).isNotNull();
//		assertThat(response.getLatitude()).isEqualTo("123.123");
//		assertThat(response.getLongitude()).isEqualTo("789.789");
//		assertThat(response.getPolygonCoordinates()).isEqualTo("[]");
//		assertThat(response.getProjectId()).isEqualTo(1L);
//		assertThat(response.getProjectName()).isEqualTo("MyProject");
//		assertThat(response.getProjectStatus()).isEqualTo(CommonStatus.ACTIVE);
//	}
//}
