//package com.sunseed.serviceImpl;
//
//
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//
//import java.util.Collections;
//import java.util.Optional;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.modelmapper.ModelMapper;
//
//import com.sunseed.entity.AgriGeneralParameter;
//import com.sunseed.entity.AgriPvProtectionHeight;
//import com.sunseed.entity.BedParameter;
//import com.sunseed.entity.Irrigation;
//import com.sunseed.entity.Projects;
//import com.sunseed.entity.ProtectionLayer;
//import com.sunseed.entity.SoilType;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.PreProcessorStatus;
//import com.sunseed.enums.TempControl;
//import com.sunseed.exceptions.ResourceNotFoundException;
//import com.sunseed.exceptions.UnprocessableException;
//import com.sunseed.model.requestDTO.AgriGeneralParameterRequestDto;
//import com.sunseed.model.requestDTO.AgriPvProtectionHeightRequestDto;
//import com.sunseed.model.responseDTO.AgriGeneralParameterResponseDto;
//import com.sunseed.repository.AgriGeneralParameterRepo;
//import com.sunseed.repository.AgriPvProtectionHeightRepo;
//import com.sunseed.repository.BedParameterRepo;
//import com.sunseed.repository.IrrigationRepo;
//import com.sunseed.repository.ProjectsRepository;
//import com.sunseed.repository.ProtectionLayerRepo;
//import com.sunseed.repository.SoilTypeRepo;
//import com.sunseed.repository.UserProfileRepository;
//
//@ExtendWith(MockitoExtension.class)
//class AgriGeneralParameterImplTest {
//
//    @Mock
//    private AgriGeneralParameterRepo agriGeneralParameterRepo;
//
//    @Mock
//    private BedParameterRepo bedParameterRepo;
//
//    @Mock
//    private IrrigationRepo irrigationRepo;
//
//    @Mock
//    private AgriPvProtectionHeightRepo agriPvProtectionHeightRepo;
//
//    @Mock
//    private ProtectionLayerRepo protectionLayerRepo;
//
//    @Mock
//    private SoilTypeRepo soilTypeRepo;
//
//    @Mock
//    private ProjectsRepository projectRepo;
//
//    @Mock
//    private UserProfileRepository userProfileRepo;
//
//    @Mock
//    private ModelMapper modelMapper;
//
//    @InjectMocks
//    private AgriGeneralParameterImpl agriGeneralParameterService;
//
//    private AgriGeneralParameterRequestDto requestDto;
//    private AgriGeneralParameter agriGeneralParameter;
//    private UserProfile userProfile;
//    private Projects project;
//    private Irrigation irrigation;
//    private SoilType soilType;
//    private BedParameter bedParameter;
//
//    @BeforeEach
//    void setUp() {
//        requestDto = new AgriGeneralParameterRequestDto();
//        requestDto.setIrrigationTypeId(1L);
//        requestDto.setSoilId(1L);
//        requestDto.setMulching(true);
//        requestDto.setTempControl("TRAIL_MIN_MAX");
//        requestDto.setTrail(1);
//        requestDto.setMinTemp(10);
//        requestDto.setMaxTemp(30);
//        requestDto.setAgriPvProtectionHeight(Collections.singletonList(new AgriPvProtectionHeightRequestDto(1L, false, 1L, 15)));
//        requestDto.setBedAngle(100);
//        requestDto.setBedAzimuth(180);
//        requestDto.setBedcc(10.0);
//        requestDto.setBedHeight(1.0);
//        requestDto.setBedWidth(1.0);
//
//        userProfile = new UserProfile();
//        userProfile.setUserProfileId(1L);
//
//        project = new Projects();
//        project.setProjectId(1L);
//        project.setUserProfile(userProfile);
//
//        irrigation = new Irrigation();
//        irrigation.setId(1L);
//
//        soilType = new SoilType();
//        soilType.setId(1L);
//
//        agriGeneralParameter = new AgriGeneralParameter();
//        agriGeneralParameter.setId(1L);
//        agriGeneralParameter.setIrrigationId(irrigation);
//        agriGeneralParameter.setSoilType(soilType);
//        agriGeneralParameter.setIsMulching(true);
//        agriGeneralParameter.setProject(project);
//        agriGeneralParameter.setTempControl(TempControl.TRAIL_MIN_MAX);
//        agriGeneralParameter.setTrail(1);
//        agriGeneralParameter.setMinTemp(10);
//        agriGeneralParameter.setMaxTemp(30);
//
//        bedParameter = new BedParameter();
//        bedParameter.setBedWidth(1.0);
//        bedParameter.setBedAngle(100);
//        bedParameter.setBedAzimuth(180);
//        bedParameter.setBedcc(10.0);
//        bedParameter.setBedHeight(1.0);
//        bedParameter.setAgriGeneralParameter(agriGeneralParameter);
//
//
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_Success() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.of(soilType));
//        when(agriGeneralParameterRepo.save(any(AgriGeneralParameter.class))).thenReturn(agriGeneralParameter);
//        when(protectionLayerRepo.findById(1L)).thenReturn(Optional.of(new ProtectionLayer()));
//        when(bedParameterRepo.save(any(BedParameter.class))).thenReturn(bedParameter);
//        //   when(modelMapper.map(any(BedParameter.class), eq(BedParameterResponseDto.class)))
//        //          .thenReturn(new BedParameterResponseDto());
//
//        AgriGeneralParameterResponseDto response = agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//System.out.println(response.getTempControl());
//        assertNotNull(response);
//        assertEquals(1L, response.getId());
//        assertEquals(1L, response.getIrrigationType());
//        assertEquals(1L, response.getSoilId());
//        assertEquals("Trail Min Max", response.getTempControl());
//        assertEquals(1, response.getTrail());
//        assertEquals(10, response.getMinTemp());
//        assertEquals(30, response.getMaxTemp());
//        assertTrue(response.isMulching());
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_UserNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_IrrigationNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_ProjectNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_SoilTypeNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testSaveAgriGeneralParameter_ProjectMismatch() {
//        UserProfile anotherUserProfile = new UserProfile();
//        anotherUserProfile.setUserProfileId(2L);
//        Projects anotherProject = new Projects();
//        anotherProject.setProjectId(2L);
//        anotherProject.setUserProfile(anotherUserProfile);
//
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(anotherProject));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.of(soilType));
//
//        assertThrows(UnprocessableException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//
//    // for update service method
//    @Test
//    void testUpdateAgriGeneralParameter_Success() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.of(agriGeneralParameter));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.of(soilType));
//        when(agriGeneralParameterRepo.save(any(AgriGeneralParameter.class))).thenReturn(agriGeneralParameter);
//        when(protectionLayerRepo.findById(1L)).thenReturn(Optional.of(new ProtectionLayer()));
//        when(agriPvProtectionHeightRepo.save(any(AgriPvProtectionHeight.class))).thenReturn(new AgriPvProtectionHeight());
//        when(agriPvProtectionHeightRepo.findById(1L)).thenReturn(Optional.of(new AgriPvProtectionHeight()));
//        when(bedParameterRepo.findByAgriGeneralParameter(any(AgriGeneralParameter.class))).thenReturn(Optional.of(bedParameter));
//        when(bedParameterRepo.save(any(BedParameter.class))).thenReturn(bedParameter);
//       // when(modelMapper.map(any(BedParameter.class), eq(BedParameterResponseDto.class))).thenReturn(new BedParameterResponseDto());
//
//        AgriGeneralParameterResponseDto response = agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//
//        assertNotNull(response);
//        assertEquals(1L, response.getId());
//        assertEquals(1L, response.getIrrigationType());
//        assertEquals(1L, response.getSoilId());
//     //   assertEquals("Trail Min Max", response.getTempControl());
//        assertEquals(1, response.getTrail());
//        assertEquals(10, response.getMinTemp());
//        assertEquals(30, response.getMaxTemp());
//        assertTrue(response.isMulching());
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_UserNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_GeneralParameterNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_IrrigationNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.of(agriGeneralParameter));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_ProjectNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.of(agriGeneralParameter));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_SoilTypeNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.of(agriGeneralParameter));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.updateAgriGeneralParameter(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateAgriGeneralParameter_ProjectMismatch() {
//        UserProfile anotherUserProfile = new UserProfile();
//        anotherUserProfile.setUserProfileId(2L);
//        Projects anotherProject = new Projects();
//        anotherProject.setProjectId(2L);
//        anotherProject.setUserProfile(anotherUserProfile);
//
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(irrigationRepo.findById(1L)).thenReturn(Optional.of(irrigation));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(anotherProject));
//        when(soilTypeRepo.findById(1L)).thenReturn(Optional.of(soilType));
//
//        assertThrows(UnprocessableException.class, () -> {
//            agriGeneralParameterService.saveAgriGeneralParameter(requestDto, 1L, 1L);
//        });
//    }
//    @Test
//    void getAgriGeneralParameter_Success() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(agriGeneralParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT)).thenReturn(Optional.of(agriGeneralParameter));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(bedParameterRepo.findByAgriGeneralParameter(agriGeneralParameter)).thenReturn(Optional.of(bedParameter));
//
//
//
//        AgriGeneralParameterResponseDto response = agriGeneralParameterService.getAgriGeneralParameter(1L,1L);
//System.out.println(response);
//        assertNotNull(response);
//        assertEquals(1L, response.getId());
//        assertEquals(1L, response.getIrrigationType());
//        assertEquals(1L, response.getSoilId());
//        assertEquals("Trail Min Max", response.getTempControl());
//        assertEquals(1, response.getTrail());
//        assertEquals(10, response.getMinTemp());
//        assertEquals(30, response.getMaxTemp());
//        assertTrue(response.isMulching());
//    }
//
//    @Test
//    void getAgriGeneralParameter_NotFound() {
//   //     when(agriGeneralParameterRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            agriGeneralParameterService.getAgriGeneralParameter(1L,1L);
//        });
//    }
//
//}