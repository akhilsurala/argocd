//package com.sunseed.serviceImpl;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//import com.sunseed.entity.Projects;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.exceptions.InvalidDataException;
//import com.sunseed.exceptions.UnAuthorizedException;
//import com.sunseed.exceptions.UnprocessableException;
//import com.sunseed.helper.PostProcessingFunctionsHelper;
//import com.sunseed.model.requestDTO.HourlyDetailsPayload;
//import com.sunseed.repository.ProjectsRepository;
//import com.sunseed.repository.RunsRepository;
//import com.sunseed.repository.UserProfileRepository;
//import com.sunseed.serviceImpl.PostprocessingDetailsServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.junit.jupiter.params.provider.ValueSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.RowMapper;
//
//import java.io.BufferedReader;
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.lang.reflect.Field;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.util.*;
//
//class PostprocessingDetailsServiceImplTest {
//
//    @InjectMocks
//    private PostprocessingDetailsServiceImpl postprocessingDetailsService;
//
//    @Mock
//    private RunsRepository runsRepository;
//
//    @Mock
//    private UserProfileRepository userProfileRepository;
//
//    @Mock
//    private JdbcTemplate jdbcTemplate;
//    @Mock
//    private HttpClient httpClient;
//
//    @Mock
//    private PostProcessingFunctionsHelper postProcessingFunctionsHelper;
//
//    @Mock
//    private ProjectsRepository projectsRepository;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    // Test: User authorization failure
//    @Test
//    void testGetHourlyDetails_userNotAuthorized_throwsException() {
//        Long userId = 1L;
//        Long projectId = 2L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        when(projectsRepository.findById(projectId)).thenReturn(Optional.of(mockProjectWithDifferentUser(userId + 1)));
//
//        Exception exception = assertThrows(UnAuthorizedException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, "Daily Air Temp", "with in run", "hourly", projectId, userId));
//
//        assertEquals("user.not.authorized", exception.getMessage());
//    }
//
//    // Test: Invalid runId list
//    @Test
//    void testGetHourlyDetails_emptyRunIdList_throwsException() {
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.emptyList());
//        when(projectsRepository.findById(1L)).thenReturn(Optional.of(mockProjectWithUser(1L)));
//
//        Exception exception = assertThrows(InvalidDataException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, "Daily Air Temp", "with in run", "hourly", 1L, 1L));
//
//        assertEquals("runId.list", exception.getMessage());
//    }
//
//
//    //    // Test: Invalid frequency
//    @Test
//    void testGetHourlyDetails_invalidFrequency_throwsException() {
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        when(projectsRepository.findById(anyLong())).thenReturn(Optional.of(mockProjectWithUser(1L)));
//
//        Exception exception = assertThrows(UnprocessableException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, "Daily Air Temp", "with in run", "invalid_frequency", 1L, 1L));
//
//        assertEquals("frequency.invalid", exception.getMessage());
//    }
//
//
//    // invalid quantity
//    @Test
//    void testGetHourlyDetails_invalidQuantity_throwsException() {
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        when(projectsRepository.findById(anyLong())).thenReturn(Optional.of(mockProjectWithUser(1L)));
//
//        Exception exception = assertThrows(UnprocessableException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, "invalid quantity", "with in run", "hourly", 1L, 1L));
//
//        assertEquals("valid.quantity", exception.getMessage());
//    }
//
//    // Test: Valid request for hourly data
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "Diffuse Horizontal Radiation",
//            "Direct Normal Radiation",
//            "Daily Air Temp",
//            "Humidity"
//    })
//    void testGetHourlyDetails_Daily_Air_Temp_returnsData(String quantity) throws Exception {
//        // Arrange
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        // Mock database query
//        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class)))
//                .thenReturn(new Object[]{1L, "https://mock-url.com/file.epw"});
//
//        // Mock HttpClient response
//        HttpResponse<InputStream> httpResponse = mock(HttpResponse.class);
//        when(httpResponse.statusCode()).thenReturn(200);
//        when(httpResponse.body()).thenReturn(mockInputStream());
//
//        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
//                .thenReturn(httpResponse);
//
//        // Act
//        //    String quantity = "Daily Air Temp"; // This determines the target column index
//        List<?> result = postprocessingDetailsService.getHourlyDetails(
//                payload, quantity, "with in run", "hourly", projectId, userId);
//
//        // Assert
//        assertNotNull(result);
//        assertFalse(result.isEmpty());
//        assertTrue(result.get(0) instanceof List); // Validate parsed data structure
//
//        // Verify the correct target column index
//        verify(httpClient).send(argThat(request ->
//                        request.uri().toString().equals("https://mock-url.com/file.epw")),
//                any(HttpResponse.BodyHandler.class));
//    }
//
//
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "Diffuse Horizontal Radiation",
//            "Direct Normal Radiation",
//            "Daily Air Temp",
//            "Humidity"
//    })
//    void testGetHourlyDetails_Run_Not_Simulate_ThrowsException(String quantity) throws Exception {
//        // Arrange
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        // Mock database query
//        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class)))
//                .thenReturn(new Object[]{0L, "https://mock-url.com/file.epw"});
//
//        // Mock HttpClient response
//        HttpResponse<InputStream> httpResponse = mock(HttpResponse.class);
//        when(httpResponse.statusCode()).thenReturn(200);
//        when(httpResponse.body()).thenReturn(mockInputStream());
//
//        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
//                .thenReturn(httpResponse);
//
//        // Act and Assert
//        Exception exception = assertThrows(UnprocessableException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId));
//
//        assertEquals("run.simulate", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "Diffuse Horizontal Radiation",
//            "Direct Normal Radiation",
//            "Daily Air Temp",
//            "Humidity"
//    })
//    void testGetHourlyDetails_File_Empty_ThrowsException(String quantity) throws Exception {
//        // Arrange
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        // Mock database query
//        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class)))
//                .thenReturn(new Object[]{1L, ""});
//
//        // Mock HttpClient response
//        HttpResponse<InputStream> httpResponse = mock(HttpResponse.class);
//        when(httpResponse.statusCode()).thenReturn(200);
//        when(httpResponse.body()).thenReturn(mockInputStream());
//
//        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
//                .thenReturn(httpResponse);
//
//        // Act and Assert
//        Exception exception = assertThrows(UnprocessableException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId));
//
//        assertEquals("file.not.found", exception.getMessage());
//    }
//
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "Diffuse Horizontal Radiation",
//            "Direct Normal Radiation",
//            "Daily Air Temp",
//            "Humidity"
//    })
//    void testGetHourlyDetails_Negative_Input_ThrowsException(String quantity) throws Exception {
//        // Arrange
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        // Mock database query
//        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), any(RowMapper.class)))
//                .thenReturn(new Object[]{1L, "https://mock-url.com/file.epw"});
//
//        // Mock HttpClient response
//        HttpResponse<InputStream> httpResponse = mock(HttpResponse.class);
//        when(httpResponse.statusCode()).thenReturn(200);
//        when(httpResponse.body()).thenReturn(mockNegativeInputStream());
//
//        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
//                .thenReturn(httpResponse);
//
//        // Act and Assert
//        Exception exception = assertThrows(UnprocessableException.class, () ->
//                postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId));
//
//        assertEquals("data.negative", exception.getMessage());
//    }
//
//    //************ test cases for pv revenue ***************
//    @ParameterizedTest
//    @ValueSource(strings = {
//            "PV Revenue Per Mega Watt",
//            "PV Revenue Per Acre"
//    })
//    void testGetPvRevenue_Success(String quantity) throws Exception {
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        // Mock data
//        List<Map<String, Object>> mockQueryResults = List.of(
//                Map.of("hour", 0, "dc_output", 34.5),
//                Map.of("hour", 1, "dc_output", 30.0)
//        );
//        Map<Integer, Double> mockTariffData = Map.of(
//                0, 30.0,
//                1, 40.0
//        );
//
//        // Mocking helper calls
//        when(postProcessingFunctionsHelper.getDcOutPutForPvRevenue(anyLong()))
//                .thenReturn(mockQueryResults);
//        when(postProcessingFunctionsHelper.getTariffData(anyLong()))
//                .thenReturn(mockTariffData);
//
//        // Call the method
//        List<Map<String, Object>> queryResults = postProcessingFunctionsHelper.getDcOutPutForPvRevenue(1L);
//        Map<Integer, Double> tariffData = postProcessingFunctionsHelper.getTariffData(1L);
//        List<?> result = postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        // Assertions
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(2235.0, result.get(0)); // Expected revenue: (34.5 * 30) + (30.0 * 40)
//    }
//
//    @Test
//    void testGetPvRevenue_EmptyResultSet() {
//
//
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//
//        // Mock data
//        List<Map<String, Object>> mockQueryResults = Collections.emptyList();
//        Map<Integer, Double> mockTariffData = Map.of(0, 30.0);
//
//        // Mocking helper calls
//        when(postProcessingFunctionsHelper.getDcOutPutForPvRevenue(anyLong()))
//                .thenReturn(mockQueryResults);
//        when(postProcessingFunctionsHelper.getTariffData(anyLong()))
//                .thenReturn(mockTariffData);
//
//        String quantity = "PV Revenue";
//        // Verify exception is thrown
//        UnprocessableException exception = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("dcoutput.result.null", exception.getMessage());
//    }
//
//    @Test
//    void testGetPvRevenue_EmptyTariff() {
//
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//
//        // Mock data
//        List<Map<String, Object>> mockQueryResults = List.of(
//                Map.of("hour", 0, "dc_output", 34.5)
//        );
//        Map<Integer, Double> mockTariffData = Collections.emptyMap();
//
//        // Mocking helper calls
//        when(postProcessingFunctionsHelper.getDcOutPutForPvRevenue(anyLong()))
//                .thenReturn(mockQueryResults);
//        when(postProcessingFunctionsHelper.getTariffData(anyLong()))
//                .thenReturn(mockTariffData);
//
//        // Verify exception is thrown
//        String quantity = "PV Revenue";
//
//        UnprocessableException exception = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("tariff.empty", exception.getMessage());
//    }
//
//    @Test
//    void testGetPvRevenue_NullInputs() {
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//
//        String quantity = "PV Revenue";
//
//        // Verify exception for null inputs
//        UnprocessableException exception1 = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("dcoutput.result.null", exception1.getMessage());
//
//        UnprocessableException exception2 = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("tariff.empty", exception2.getMessage());
//    }
//
//    @Test
//    void testGetPvRevenue_Null_Tariff() {
//        Long projectId = 1L;
//        Long userId = 1L;
//        HourlyDetailsPayload payload = new HourlyDetailsPayload();
//        payload.setRunIds(Collections.singletonList(1L));
//
//        // Mock project and user validation
//        when(projectsRepository.findById(projectId))
//                .thenReturn(Optional.of(mockProjectWithUser(userId)));
//        // Mock data
//        List<Map<String, Object>> mockQueryResults = List.of(
//                Map.of("hour", 0, "dc_output", 34.5)
//        );
//        Map<Integer, Double> mockTariffData = null;
//
//        // Mocking helper calls
//        when(postProcessingFunctionsHelper.getDcOutPutForPvRevenue(anyLong()))
//                .thenReturn(mockQueryResults);
//        when(postProcessingFunctionsHelper.getTariffData(anyLong()))
//                .thenReturn(mockTariffData);
//
//
//        String quantity = "PV Revenue";
//
//        // Verify exception for null inputs
//        UnprocessableException exception1 = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("dcoutput.result.null", exception1.getMessage());
//
//        UnprocessableException exception2 = assertThrows(UnprocessableException.class, () -> {
//            postprocessingDetailsService.getHourlyDetails(payload, quantity, "with in run", "hourly", projectId, userId);
//        });
//
//        assertEquals("tariff.empty", exception2.getMessage());
//    }
//
//
//    // Mock project with a different user
//    private Projects mockProjectWithDifferentUser(Long userId) {
//        Projects project = new Projects();
//        UserProfile userProfile = new UserProfile();
//        userProfile.setUserId(userId);
//        project.setUserProfile(userProfile);
//        return project;
//    }
//
//    // Mock project with the same user
//    private Projects mockProjectWithUser(Long userId) {
//        Projects project = new Projects();
//        UserProfile userProfile = new UserProfile();
//        userProfile.setUserId(userId);
//        project.setProjectId(1L);
//        project.setUserProfile(userProfile);
//        return project;
//    }
//
//    // Mock InputStream for EPW file
//    private InputStream mockInputStream() {
//        String mockData = "Line 1: Header info\n" +
//                "Line 2: Header info\n" +
//                "Line 3: Header info\n" +
//                "Line 4: Header info\n" +
//                "Line 5: Header info\n" +
//                "Line 6: Header info\n" +
//                "Line 7: Header info\n" +
////                        "Line 8: Header info\n" +
//                "Year,Month,Day,Hour,Minute,abc,Dry Bulb Temp,abc,Humidity,abc,abc,abc,abc,abc,Direct Normal Radiation,Diffuse Horizontal Radiation\n" +
//                "2024,12,11,1,0,25.6,45.3,0,13.4,0,0,0,0,0,500,100\n" +
//                "2024,12,11,2,0,26.0,46.4,0,12.5,0,0,0,0,0,510,105\n" +
//                "2024,12,11,3,0,24.5,43.5,0,10.4,0,0,0,0,0,490,95\n";
//
//        return new ByteArrayInputStream(mockData.getBytes(StandardCharsets.UTF_8));
//    }
//
//
//    // file with negative data
//    private InputStream mockNegativeInputStream() {
//        String mockData = "Line 1: Header info\n" +
//                "Line 2: Header info\n" +
//                "Line 3: Header info\n" +
//                "Line 4: Header info\n" +
//                "Line 5: Header info\n" +
//                "Line 6: Header info\n" +
//                "Line 7: Header info\n" +
////                        "Line 8: Header info\n" +
//                "Year,Month,Day,Hour,Minute,abc,Dry Bulb Temp,abc,Humidity,abc,abc,abc,abc,abc,Direct Normal Radiation,Diffuse Horizontal Radiation\n" +
//                "2024,12,11,1,0,-25.6,-45.3,0,-13.4,0,0,0,0,0,-500,-100\n" +
//                "2024,12,11,2,0,-26.0,-46.4,0,-12.5,0,0,0,0,0,-510,-105\n" +
//                "2024,12,11,3,0,-24.5,-43.5,0,-10.4,0,0,0,0,0,-490,-95\n";
//
//        return new ByteArrayInputStream(mockData.getBytes(StandardCharsets.UTF_8));
//    }
//
//}
