//package com.sunseed.controller;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.BDDMockito.given;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunseed.controller.project.ProjectsController;
//import com.sunseed.enums.CommonStatus;
//import com.sunseed.exceptions.GlobalExceptionHandler;
//import com.sunseed.model.Coordinates;
//import com.sunseed.model.requestDTO.ProjectsCreationRequestDto;
//import com.sunseed.model.requestDTO.ProjectsUpdationRequestDto;
//import com.sunseed.model.responseDTO.ProjectsCreationResponseDto;
//import com.sunseed.model.responseDTO.ProjectsListResponseDto;
//import com.sunseed.model.responseDTO.ProjectsUpdationResponseDto;
//import com.sunseed.response.ApiResponse;
//import com.sunseed.service.ProjectsService;
//
//@ExtendWith(MockitoExtension.class)
//public class ProjectsControllerTests {
//
//	@Mock
//	private ProjectsService projectsService;
//	@Mock
//	private ApiResponse apiResponse;
//
//	@InjectMocks
//	private ProjectsController projectsController;
//
//	private MockMvc mockMvc;
//	private ObjectMapper objectMapper;
//
//	@BeforeEach
//	public void setUp() {
//
//		GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
//		globalExceptionHandler.setApiResponse(apiResponse);
//
//		mockMvc = MockMvcBuilders.standaloneSetup(projectsController).setControllerAdvice(globalExceptionHandler)
//				.build();
//		objectMapper = new ObjectMapper();
//	}
//
//	/*
//	 * Test cases for createProject() method for post mapping
//	 */
//
//	@DisplayName("Test case for create project method, Project name null")
//	@Test
//	public void testCreateProjectMethod_nullProjectName() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName(null);
//		requestDto.setLatitude(123.456 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Project name should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Project name should not be null");
//	}
//
//	@DisplayName("Test case for create project method , Invalid project name")
//	@ParameterizedTest
//	@ValueSource(strings = { "Project$One", "Project two", "3", "", "ppppppppppppppppppppppppppppppp", " ", "Proj" })
//	public void testCreateProjectMethod_invalidProjectName(String projectName) throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName(projectName);
//		requestDto.setLatitude(123.456 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Invalid Project name");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Invalid Project name");
//	}
//
//	@DisplayName("Test case for create project method, latitude null")
//	@Test
//	public void testCreateProjectMethod_nullLatitude() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(null);
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Latitude should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Latitude should not be null");
//	}
//
//	@DisplayName("Test case for create project method , Invalid latitude")
//	@ParameterizedTest
//	@ValueSource(strings = { "a", "123", "12.23.2", " ", "" })
//	public void testCreateProjectMethod_invalidLatitude(String latitude) throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(latitude);
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Invalid Latitude");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Invalid Latitude");
//	}
//
//	@DisplayName("Test case for create project method, longitude null")
//	@Test
//	public void testCreateProjectMethod_nullLongitude() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.123 + "");
//		requestDto.setLongitude(null);
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Longitude should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Longitude should not be null");
//	}
//
//	@DisplayName("Test case for create project method , Invalid longitude")
//	@ParameterizedTest
//	@ValueSource(strings = { "a", "123", "12.23.2", " ", "" })
//	public void testCreateProjectMethod_invalidLongitude(String longitude) throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.123 + "");
//		requestDto.setLongitude(longitude);
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Invalid Longitude");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Invalid Longitude");
//	}
//
//	@DisplayName("Test case for create project method, polygon coordinates null")
//	@Test
//	public void testCreateProjectMethod_nullPolygonCoordinates() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.123 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(null);
//		requestDto.setArea(0.5);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Polygon Coordinates should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Polygon Coordinates should not be null");
//	}
//
//	@DisplayName("Test case for create project method, area null")
//	@Test
//	public void testCreateProjectMethod_nullArea() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.415 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(null);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Area should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Area should not be null");
//	}
//
//	@DisplayName("Test case for create project method , Invalid area value")
//	@ParameterizedTest
//	@ValueSource(strings = { "0.09", "20.01", "21", "-0.2" })
//	public void testCreateProjectMethod_invalidAreaValue(Double area) throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.12 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(area);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Area is not in range");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isBadRequest()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Area is not in range");
//	}
//
//	@DisplayName("Test case for create project method , Invalid area format")
//	@ParameterizedTest
//	@ValueSource(strings = { "a", " ", "" })
//	public void testCreateProjectMethod_invalidAreaFormat(String invalidArea) throws Exception {
//
//		/// when and then
//		assertThrows(NumberFormatException.class, () -> {
//			ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//			requestDto.setProjectName("MyProject");
//			requestDto.setLatitude(123.12 + "");
//			requestDto.setLongitude(789.012 + "");
//			requestDto.setPolygonCoordinates(List.of());
//			// Attempt to parse the invalid area value
//			requestDto.setArea(Double.valueOf(invalidArea));
//		});
//	}
//
//	@DisplayName("Test case for create project method, successful project creation")
//	@Test
//	public void testCreateProjectMethod_successfulProjectCreation() throws Exception {
//
//		// given
//		ProjectsCreationRequestDto requestDto = new ProjectsCreationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setLatitude(123.456 + "");
//		requestDto.setLongitude(789.012 + "");
//		requestDto.setPolygonCoordinates(List.of());
//		requestDto.setArea(0.5);
//
//		ProjectsCreationResponseDto responseDto = ProjectsCreationResponseDto.builder().projectId(1L)
//				.projectName("MyProject").projectStatus(CommonStatus.ACTIVE.getValue())
//				.polygonCoordinates(new Coordinates[0]).latitude(123.456 + "").longitude(789.012 + "").build();
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		MockHttpServletRequest request = new MockHttpServletRequest();
//		request.setAttribute("userId", 123L);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", responseDto);
//		mapResponse.put("message", "Project created successfully");
//		mapResponse.put("httpStatus", HttpStatus.CREATED);
//
//		given(projectsService.createProject(eq(requestDto), eq(123L))).willReturn(responseDto);
//
//		given(apiResponse.commonResponseHandler(any(), any(), eq(HttpStatus.CREATED)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.CREATED));
//
//		// when
//		MvcResult result = mockMvc
//				.perform(MockMvcRequestBuilders.post("/v1/project").content(requestBody)
//						.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L))
//				.andExpect(status().isCreated()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		ProjectsCreationResponseDto responseData = objectMapper.convertValue(responseMap.get("data"),
//				ProjectsCreationResponseDto.class);
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(201);
//		assertThat(responseString).isNotNull();
//		assertNotNull(responseData);
//		assertThat(httpStatus).isEqualTo(HttpStatus.CREATED.name());
//		assertThat(message).isEqualTo("Project created successfully");
//		assertThat(responseData.getProjectId()).isEqualTo(1L);
//		assertThat(responseData.getProjectName()).isEqualTo("MyProject");
//	}
//
//	/*
//	 * Test cases for updateProject() method
//	 */
//
//	@DisplayName("Test case for update project method, Project name null")
//	@Test
//	public void updateProjectMethod_nullProjectName() throws Exception {
//
//		// given
//		ProjectsUpdationRequestDto requestDto = new ProjectsUpdationRequestDto();
//		requestDto.setProjectName(null);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Project name should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/{projectId}", 1L)
//				.content(requestBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Project name should not be null");
//	}
//
//	@DisplayName("Test case for update project method , Invalid project name")
//	@ParameterizedTest
//	@ValueSource(strings = { "Project$One", "Project two", "3", "", "ppppppppppppppppppppppppppppppp", " ", "Proj" })
//	public void testupdateProjectMethod_invalidProjectName(String projectName) throws Exception {
//
//		// given
//		ProjectsUpdationRequestDto requestDto = new ProjectsUpdationRequestDto();
//		requestDto.setProjectName(projectName);
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Invalid Project name");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/{projectId}", 1L)
//				.content(requestBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Invalid Project name");
//	}
//
//	@DisplayName("Test case for update project method , update project name only")
//	@Test
//	public void testupdateProjectMethod_updatedProjectNameOnly() throws Exception {
//
//		// given
//		ProjectsUpdationRequestDto requestDto = new ProjectsUpdationRequestDto();
//		requestDto.setProjectName("MyProject");
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		ProjectsUpdationResponseDto projectsUpdationResponseDto = ProjectsUpdationResponseDto.builder().projectId(1L)
//				.comments("my project").latitude(12.12 + "").longitude(78.78 + "")
//				.polygonCoordinates(new Coordinates[0]).projectName("MyProject")
//				.projectStatus(CommonStatus.ACTIVE.getValue()).build();
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", projectsUpdationResponseDto);
//		mapResponse.put("message", "Project updated successfully");
//		mapResponse.put("httpStatus", HttpStatus.OK);
//
//		given(projectsService.updateProject(eq(requestDto), eq(1L))).willReturn(projectsUpdationResponseDto);
//
//		given(apiResponse.commonResponseHandler(any(), any(), eq(HttpStatus.OK)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.OK));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/{projectId}", 1L)
//				.content(requestBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		ProjectsUpdationResponseDto responseData = objectMapper.convertValue(responseMap.get("data"),
//				ProjectsUpdationResponseDto.class);
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(200);
//		assertThat(responseString).isNotNull();
//		assertNotNull(responseData);
//		assertThat(httpStatus).isEqualTo(HttpStatus.OK.name());
//		assertThat(message).isEqualTo("Project updated successfully");
//		assertThat(responseData.getProjectId()).isEqualTo(1L);
//		assertThat(responseData.getProjectName()).isEqualTo("MyProject");
//	}
//
//	@DisplayName("Test case for update project method , update comments only without project name")
//	@Test
//	public void testupdateProjectMethod_updatedCommentsOnly_withoutProjectName() throws Exception {
//
//		// given
//		ProjectsUpdationRequestDto requestDto = new ProjectsUpdationRequestDto();
//		requestDto.setComments("This is my project");
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", null);
//		mapResponse.put("message", "Project name should not be null");
//		mapResponse.put("httpStatus", HttpStatus.BAD_REQUEST);
//
//		given(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.BAD_REQUEST));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/{projectId}", 1L)
//				.content(requestBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest())
//				.andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		Object data = responseMap.get("data");
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(400);
//		assertThat(responseString).isNotNull();
//		assertNull(data);
//		assertThat(httpStatus).isEqualTo(HttpStatus.BAD_REQUEST.name());
//		assertThat(message).isEqualTo("Project name should not be null");
//	}
//
//	@DisplayName("Test case for update project method , update comments with project name")
//	@Test
//	public void testupdateProjectMethod_updatedComments_withProjectName() throws Exception {
//
//		// given
//		ProjectsUpdationRequestDto requestDto = new ProjectsUpdationRequestDto();
//		requestDto.setProjectName("MyProject");
//		requestDto.setComments("This is my project");
//
//		String requestBody = objectMapper.writeValueAsString(requestDto);
//
//		ProjectsUpdationResponseDto projectsUpdationResponseDto = ProjectsUpdationResponseDto.builder().projectId(1L)
//				.comments("This is my project").latitude(12.12 + "").longitude(78.78 + "")
//				.polygonCoordinates(new Coordinates[0]).projectName("MyProject")
//				.projectStatus(CommonStatus.ACTIVE.getValue()).build();
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", projectsUpdationResponseDto);
//		mapResponse.put("message", "Project updated successfully");
//		mapResponse.put("httpStatus", HttpStatus.OK);
//
//		given(projectsService.updateProject(eq(requestDto), eq(1L))).willReturn(projectsUpdationResponseDto);
//
//		given(apiResponse.commonResponseHandler(any(), any(), eq(HttpStatus.OK)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.OK));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/{projectId}", 1L)
//				.content(requestBody).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		ProjectsUpdationResponseDto responseData = objectMapper.convertValue(responseMap.get("data"),
//				ProjectsUpdationResponseDto.class);
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(200);
//		assertThat(responseString).isNotNull();
//		assertNotNull(responseData);
//		assertThat(httpStatus).isEqualTo(HttpStatus.OK.name());
//		assertThat(message).isEqualTo("Project updated successfully");
//		assertThat(responseData.getProjectId()).isEqualTo(1L);
//		assertThat(responseData.getComments()).isEqualTo("This is my project");
//	}
//
//	/*
//	 * Test cases for get projects method
//	 */
//
//	@Test
//	public void testGetAllProjectsMethod_whenUserHasNoProjects() throws Exception {
//
//		// given
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", List.of());
//		mapResponse.put("message", "Projects fetched successfully");
//		mapResponse.put("httpStatus", HttpStatus.OK);
//
//		given(projectsService.getAllProjects(eq(123L))).willReturn(List.of());
//
//		given(apiResponse.commonResponseHandler(any(), any(), eq(HttpStatus.OK)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.OK));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/projects")
//				.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isOk())
//				.andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		List<ProjectsListResponseDto> projectsList = objectMapper.convertValue(responseMap.get("data"),
//				new TypeReference<List<ProjectsListResponseDto>>() {
//				});
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(200);
//		assertThat(responseString).isNotNull();
//		assertNotNull(projectsList);
//		assertThat(projectsList).isEmpty();
//		assertThat(projectsList.size()).isEqualTo(0);
//		assertThat(httpStatus).isEqualTo(HttpStatus.OK.name());
//		assertThat(message).isEqualTo("Projects fetched successfully");
//	}
//
//	@Test
//	public void testGetAllProjectsMethod_whenUserHasMultipleProjects() throws Exception {
//
//		// given
//		ProjectsListResponseDto project1 = ProjectsListResponseDto.builder()
//				.comments("project 1").projectId(1L).projectName("Project1").build();
//		ProjectsListResponseDto project2 = ProjectsListResponseDto.builder()
//				.comments("project 2").projectId(2L).projectName("Project2").build();
//
//		List<ProjectsListResponseDto> projects = List.of(project1,project2);
//
//		Map<String, Object> mapResponse = new HashMap<>();
//		mapResponse.put("data", projects);
//		mapResponse.put("message", "Projects fetched successfully");
//		mapResponse.put("httpStatus", HttpStatus.OK);
//
//		given(projectsService.getAllProjects(eq(123L))).willReturn(projects);
//
//		given(apiResponse.commonResponseHandler(any(), any(), eq(HttpStatus.OK)))
//				.willReturn(new ResponseEntity<>(mapResponse, HttpStatus.OK));
//
//		// when
//		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/v1/projects")
//				.contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isOk())
//				.andReturn();
//
//		String responseString = result.getResponse().getContentAsString();
//		Map<String, Object> responseMap = objectMapper.readValue(responseString,
//				new TypeReference<Map<String, Object>>() {
//				});
//
//		List<ProjectsListResponseDto> projectsList = objectMapper.convertValue(responseMap.get("data"),
//				new TypeReference<List<ProjectsListResponseDto>>() {
//				});
//		String httpStatus = (String) responseMap.get("httpStatus");
//		String message = (String) responseMap.get("message");
//
//		// then
//		assertThat(result).isNotNull();
//		assertThat(result.getResponse().getStatus()).isEqualTo(200);
//		assertThat(responseString).isNotNull();
//		assertNotNull(projectsList);
//		assertThat(projectsList).isNotEmpty();
//		assertThat(projectsList.size()).isEqualTo(2);
//		assertThat(httpStatus).isEqualTo(HttpStatus.OK.name());
//		assertThat(message).isEqualTo("Projects fetched successfully");
//		assertThat(projectsList.get(0)).isNotNull();
//		assertThat(projectsList.get(0).getProjectId()).isEqualTo(1L);
//		assertThat(projectsList.get(0).getComments()).isEqualTo("project 1");
//		assertThat(projectsList.get(0).getProjectName()).isEqualTo("Project1");
//		assertThat(projectsList.get(1)).isNotNull();
//		assertThat(projectsList.get(1).getProjectId()).isEqualTo(2L);
//		assertThat(projectsList.get(1).getComments()).isEqualTo("project 2");
//		assertThat(projectsList.get(1).getProjectName()).isEqualTo("Project2");
//	}
//
//}
