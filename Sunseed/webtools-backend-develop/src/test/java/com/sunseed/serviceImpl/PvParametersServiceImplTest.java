package com.sunseed.serviceImpl;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.exceptions.PvParametersException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.PvParametersRequestDto;
import com.sunseed.repository.ProjectsRepository;
import com.sunseed.repository.PvModuleConfigurationRepository;
import com.sunseed.repository.PvModuleRepository;
import com.sunseed.repository.PvParameterRepository;
import com.sunseed.repository.*;
import com.sunseed.repository.ModeOfPvOperationRepository;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.service.*;
import com.sunseed.enums.RunStatus;
import com.sunseed.enums.PVModuleConfigType;

import jakarta.validation.Validator;

import com.sunseed.entity.*;


import java.util.*;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class PvParametersServiceImplTest {

    @InjectMocks
    private PvParametersServiceImpl pvParametersService;
    
    @Mock
    private PvParametersServiceImpl pvParametersService2;

    @Mock
    private ProjectsRepository projectRepository;

    @Mock
    private PreProcessorToggleRepository preProcessorToggleRepository;

    @Mock
    private RunsRepository runRepository;

    @Mock
    private PvParameterRepository pvParameterRepository;

    @Mock
    private PvModuleRepository pvModuleRepository;

    @Mock
    private PvModuleConfigurationRepository pvModuleConfigurationRepository;

    @Mock
    private ModeOfPvOperationRepository modeOfPvOperationRepository;

    @Mock
    private AgriGeneralParameterRepo agriGeneralParameterRepository;

    @Mock
    private CropParametersRepo cropParametersRepository;

    @Mock
    private RunService runService;

    @Mock
    private Validator validator;

    @Mock
    private SoilService soilService;

    @Mock
    private SoilTypeRepo soilTypeRepo;

    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenUserIdIsInvalid() {
        assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "ONLY_PV", 1L, -1L, "create", null));
    }

    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenProjectIdIsInvalid() {
        assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), -1L, "ONLY_PV", 1L, 1L, "create", null));
    }

    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenCallForUpdateAndPvParameterIdIsInvalid() {
        assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "ONLY_PV", 1L, 1L, "update", null));
    }

    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenToggleIsInvalid() {
        assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "INVALID_TOGGLE", 1L, 1L, "create", null));
    }

    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenRunIsNotFound() {
        // Mark stubbing as lenient to avoid unnecessary stubbing exception
        lenient().when(runRepository.findById(1L)).thenReturn(Optional.empty());

        // Perform the test and assert the exception
        PvParametersException exception = assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "Only Pv", 1L, 1L, "update", 1L));

        // Assert exception message
        assertEquals("run.not.found", exception.getMessage());
    }


    @Test
    void addOrUpdatePvParametersWithToggle_shouldThrowExceptionWhenRunIsNotMasterAndToggleNotApv() {
        PvParametersException thrown = assertThrows(PvParametersException.class, () -> 
            pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "ONLY_PV", 1L, 1L, "update", 1L));

        assertEquals("toggle.not.exists", thrown.getMessage());
    }


    @Test
    void addOrUpdatePvParametersWithToggle_shouldReturnConflictWhenPvParameterAlreadyExists() {
        Projects project = new Projects();
        project.setProjectId(1L);
        
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(1L); // Setting the userId for the UserProfile
        project.setUserProfile(userProfile);  // Assigning the mocked UserProfile to the project
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        PvParameter existingPvParameter = new PvParameter();
        when(pvParameterRepository.findByProjectProjectIdAndStatus(1L, PreProcessorStatus.DRAFT))
                .thenReturn(Optional.of(existingPvParameter));

        PreProcessorToggle existingToggle = new PreProcessorToggle();
        when(preProcessorToggleRepository.findByProjectProjectIdAndPreProcessorStatus(1L, PreProcessorStatus.DRAFT))
                .thenReturn(Optional.of(existingToggle));

        Map<String, Object> response = pvParametersService.addOrUpdatePvParametersWithToggle(
                new PvParametersRequestDto(), 1L, "APV", null, 1L, "create", null);

        assertEquals(HttpStatus.CONFLICT, response.get("httpStatus"));
        assertNotNull(response.get("response"));
    }

    @Test
    void addOrUpdatePvParametersWithToggle_shouldCreatePvParametersForNewRun() {
        Projects project = new Projects();
        project.setProjectId(1L);
        
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(1L); // Setting the userId for the UserProfile
        project.setUserProfile(userProfile);  // Assigning the mocked UserProfile to the project
        
//        project.setUserProfile(new UserProfile(1L, null, null, null, null, null, null, null, null, null));

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(pvParameterRepository.findByProjectProjectIdAndStatus(1L, PreProcessorStatus.DRAFT))
                .thenReturn(Optional.empty());

        PreProcessorToggle toggle = new PreProcessorToggle();
        when(preProcessorToggleRepository.findByProjectProjectIdAndPreProcessorStatus(1L, PreProcessorStatus.DRAFT))
                .thenReturn(Optional.empty());
        when(preProcessorToggleRepository.save(any())).thenReturn(toggle);

        PvParameter pvParameter = new PvParameter();
        when(pvParameterRepository.save(any())).thenReturn(pvParameter);
        
        
        PvModule pvModule = new PvModule();
        when(pvModuleRepository.findById(anyLong())).thenReturn(Optional.of(pvModule));  // Mock the PvModule retrieval

        ModeOfPvOperation modeOfPvOperation = new ModeOfPvOperation();
        when(modeOfPvOperationRepository.findById(2L)).thenReturn(Optional.of(modeOfPvOperation));
        
//        PvModuleConfiguration moduleConfig = new PvModuleConfiguration();
//        when(pvModuleConfigurationRepository.findById(1L)).thenReturn(Optional.of(moduleConfig)); // Mock ModuleConfig

        when(pvModuleConfigurationRepository.findById(1L)).thenReturn(Optional.of(new PvModuleConfiguration()));
        when(pvModuleConfigurationRepository.findById(2L)).thenReturn(Optional.of(new PvModuleConfiguration()));

        
//        PvParametersRequestDto requestDto = mock(PvParametersRequestDto.class);
//        when(requestDto.getModeOfOperationId()).thenReturn(2L); // Mocked value
//        when(requestDto.getTiltIfFt()).thenReturn(45.0);        // Mocked value
//        when(requestDto.getPvModuleId()).thenReturn(1L);       // Mocked value
//        when(requestDto.getMaxAngleOfTracking()).thenReturn(34.0); // Mocked value
//        when(requestDto.getModuleConfigId()).thenReturn(Arrays.asList(1L, 2L));
        List<Long> value = new ArrayList<>() ;
        value.add(1L);
        value.add(2L);
        value.add(3L);
        value.add(4L);
        
        PvParametersRequestDto requestDto = new PvParametersRequestDto();
        requestDto.setModeOfOperationId(2L);
        requestDto.setTiltIfFt(45.0);
        requestDto.setPvModuleId(1L);
        requestDto.setMaxAngleOfTracking(34.0);
        requestDto.setModuleConfigId(value);


        Map<String, Object> response = pvParametersService.addOrUpdatePvParametersWithToggle(
        		requestDto, 1L, "APV", null, 1L, "create", null);

        assertEquals(HttpStatus.OK, response.get("httpStatus"));
        assertNotNull(response.get("response"));
    }

    
    @Test
    void testGetPvParametersWithToggle_whenUserIdInvalid_thenThrowException() {
        // Act & Assert
        PvParametersException exception = assertThrows(PvParametersException.class, 
            () -> pvParametersService.getPvParametersWithToggle(null, 1L, 1L));
        assertEquals("user.not.found", exception.getMessage());
    }

    @Test
    void testGetPvParametersWithToggle_whenProjectIdInvalid_thenThrowException() {
        // Act & Assert
        PvParametersException exception = assertThrows(PvParametersException.class, 
            () -> pvParametersService.getPvParametersWithToggle(1L, null, 1L));
        assertEquals("project.not.found", exception.getMessage());
    }

    @Test
    void testGetPvParametersWithToggle_whenRunIdNull_thenCreateOrUpdateDraftPreProcessorToggle() {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;
        Projects project = new Projects();
        project.setProjectId(projectId);
        UserProfile userProfile = new UserProfile();
        userProfile.setUserId(userId);
        project.setUserProfile(userProfile);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));
        PreProcessorToggle preProcessorToggle = new PreProcessorToggle();
        preProcessorToggle.setSoilType(new SoilType());  // Set SoilType or any necessary properties
        when(preProcessorToggleRepository.findByProjectProjectIdAndPreProcessorStatus(projectId, PreProcessorStatus.DRAFT))
            .thenReturn(Optional.of(preProcessorToggle)); // Ensure it returns a non-null PreProcessorToggle
        when(pvParameterRepository.findAllByProjectProjectIdAndStatus(projectId, PreProcessorStatus.DRAFT))
            .thenReturn(Collections.emptyList());

        // Act
        Map<String, Object> response = pvParametersService.getPvParametersWithToggle(userId, projectId, null);

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("response"));
        assertEquals("pvWithToggle.fetched", response.get("message"));
        assertEquals(HttpStatus.OK, response.get("httpStatus"));
    }

    @Test
    void testGetPvParametersWithToggle_whenRunExists_thenFetchPreProcessorToggleAndPvParameters() {
        // Arrange
        Long userId = 1L;
        Long projectId = 1L;
        Long runId = 1L;

        // Mock response data
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("response", new HashMap<>());
        mockResponse.put("message", "pvWithToggle.fetched");
        mockResponse.put("httpStatus", HttpStatus.OK);

        // Mock the service method to return a predefined response
        when(pvParametersService2.getPvParametersWithToggle(userId, projectId, runId))
            .thenReturn(mockResponse);

        // Act
        Map<String, Object> response = pvParametersService2.getPvParametersWithToggle(userId, projectId, runId);

        // Assert
        assertNotNull(response);
        assertTrue(response.containsKey("response"));
        assertEquals("pvWithToggle.fetched", response.get("message"));
        assertEquals(HttpStatus.OK, response.get("httpStatus"));

        // Verify that the service method was called
        verify(pvParametersService2, times(1)).getPvParametersWithToggle(userId, projectId, runId);
    }


    @Test
    void testGetMasterData_whenModeIsNull_thenReturnAllData() {
        // Arrange
        List<SoilType> soils = List.of();
        List<PvModule> pvModules = List.of(new PvModule(1L, "Module1", 10.0, 5.0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null));
        List<ModeOfPvOperation> modes = List.of();
        List<PvModuleConfiguration> configurations = List.of(new PvModuleConfiguration(1L, "Config1", 10, 0, PVModuleConfigType.L, null, null, null, null, null));

        when(soilService.getActiveSoilDetails()).thenReturn(soils);
        when(pvModuleRepository.findByIsActiveTrueAndHideFalseOrderByModuleTypeAsc()).thenReturn(pvModules);
        when(modeOfPvOperationRepository.findByIsActiveTrueAndHideFalseOrderByModeOfOperationAsc()).thenReturn(modes);
        when(pvModuleConfigurationRepository.findByIsActiveTrueAndHideFalseOrderByOrderingAsc()).thenReturn(configurations);

        // Act
        Map<String, Object> response = pvParametersService.getMasterData(null);

        // Assert
        assertNotNull(response);
        assertEquals(4, response.size());
        assertEquals(soils.size(), ((List<?>) response.get("soils")).size());
        assertEquals(pvModules.size(), ((List<?>) response.get("pvModules")).size());
        assertEquals(modes.size(), ((List<?>) response.get("modeOfOperations")).size());
        assertEquals(configurations.size(), ((List<?>) response.get("moduleConfigurations")).size());
    }

    @Test
    void testGetMasterData_whenModeIsSingleAxisTracking_thenFilterConfigurations() {
        // Arrange
        List<PvModuleConfiguration> configurations = List.of(
            new PvModuleConfiguration(1L, "Config1", 10, 0, PVModuleConfigType.L, null, null, null, null, null),
            new PvModuleConfiguration(2L, "Config2", 5, 0, PVModuleConfigType.P, null, null, null, null, null)
        );

        when(pvModuleConfigurationRepository.findByIsActiveTrueAndHideFalseOrderByOrderingAsc()).thenReturn(configurations);

        // Act
        Map<String, Object> response = pvParametersService.getMasterData("Fixed Tilt");

        // Assert
        List<?> filteredConfigurations = (List<?>) response.get("moduleConfigurations");
        assertEquals(2, filteredConfigurations.size());
    }

}
