//package com.sunseed.serviceImpl;
//import com.sunseed.entity.Currency;
//import com.sunseed.entity.EconomicParameters;
//import com.sunseed.entity.Projects;
//import com.sunseed.entity.UserProfile;
//import com.sunseed.enums.PreProcessorStatus;
//import com.sunseed.exceptions.ConflictException;
//import com.sunseed.exceptions.ResourceNotFoundException;
//import com.sunseed.exceptions.UnprocessableException;
//import com.sunseed.mappers.EconomicParameterModelMapper;
//import com.sunseed.model.requestDTO.EconomicParametersRequestDto;
//import com.sunseed.model.responseDTO.CurrencyResponse;
//import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
//import com.sunseed.repository.CurrencyRepository;
//import com.sunseed.repository.EconomicParameterRepository;
//import com.sunseed.repository.ProjectsRepository;
//import com.sunseed.repository.UserProfileRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//        import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class EconomicParametersServiceImplTest {
//
//    @Mock
//    private ProjectsRepository projectRepo;
//
//    @Mock
//    private EconomicParameterRepository economicParameterRepo;
//
//    @Mock
//    private EconomicParameterModelMapper economicModelMapper;
//
//    @Mock
//    private CurrencyRepository currencyRepo;
//
//    @Mock
//    private UserProfileRepository userProfileRepo;
//
//    @InjectMocks
//    private EconomicParametersServiceImpl economicParametersService;
//
//    private EconomicParametersRequestDto requestDto;
//    private EconomicParameters economicParameters;
//    private UserProfile userProfile;
//    private Projects project;
//    private Currency currency;
//    private EconomicParametersResponseDto economicParametersResponseDto;
//
//    @BeforeEach
//    void setUp() {
//        Integer[] hourlySellingRates = {5, 30, 57, 45};
//        requestDto = new EconomicParametersRequestDto();
//        requestDto.setCurrencyId(1L);
//        requestDto.setHourlySellingRates(hourlySellingRates);
//        requestDto.setMinInputCostOfCrop(50.0);
//        requestDto.setMaxInputCostOfCrop(150.0);
//        requestDto.setMinReferenceYieldCost(200.0);
//        requestDto.setMaxReferenceYieldCost(300.0);
//        requestDto.setMinSellingCostOfCrop(400.0);
//        requestDto.setMaxSellingCostOfCrop(500.0);
//        requestDto.setEconomicParameter(true);
//
//        userProfile = new UserProfile();
//        userProfile.setUserProfileId(1L);
//
//        project = new Projects();
//        project.setProjectId(1L);
//        project.setUserProfile(userProfile);
//
//        currency = new Currency();
//        currency.setCurrencyId(1L);
//        currency.setCurrency("INR");
//        CurrencyResponse currencyResponse=new CurrencyResponse();
//        currencyResponse.setCurrency("INR");
//        currencyResponse.setCurrencyId(1L);
//
//        economicParameters = new EconomicParameters();
//         economicParameters.setEconomicId(1L);
//        economicParameters.setCurrency(currency);
//        economicParameters.setProject(project);
//        economicParameters.setHourlySellingRates(hourlySellingRates);
//        economicParameters.setMinInputCostOfCrop(50.0);
//        economicParameters.setMaxInputCostOfCrop(150.0);
//        economicParameters.setMinReferenceYieldCost(200.0);
//        economicParameters.setMaxReferenceYieldCost(300.0);
//        economicParameters.setMinSellingCostOfCrop(400.0);
//        economicParameters.setMaxSellingCostOfCrop(500.0);
//
//        economicParameters.setEconomicParameter(true);
//
//
//
//        economicParametersResponseDto=new EconomicParametersResponseDto();
//        economicParametersResponseDto.setEconomicId(1L);
//        economicParametersResponseDto.setCurrency(currencyResponse);
//      //  economicParametersResponseDto.setProject(project);
//        economicParametersResponseDto.setHourlySellingRates(hourlySellingRates);
//        economicParametersResponseDto.setMinInputCostOfCrop(50.0);
//        economicParametersResponseDto.setMaxInputCostOfCrop(150.0);
//        economicParametersResponseDto.setMinReferenceYieldCost(200.0);
//        economicParametersResponseDto.setMaxReferenceYieldCost(300.0);
//        economicParametersResponseDto.setMinSellingCostOfCrop(400.0);
//        economicParametersResponseDto.setMaxSellingCostOfCrop(500.0);
//
//        economicParametersResponseDto.setEconomicParameter(true);
//
//    }
//
//    @Test
//    void testCreateEconomicParameters_Success() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(economicParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT)).thenReturn(Optional.empty());
//        when(economicModelMapper.economicRequestToEconomic(requestDto)).thenReturn(economicParameters);
//        when(economicParameterRepo.save(any(EconomicParameters.class))).thenReturn(economicParameters);
//        when(economicModelMapper.economicToEconomicResponse(any(EconomicParameters.class))).thenReturn(economicParametersResponseDto);
//
//        EconomicParametersResponseDto response = economicParametersService.createEconomicParameters(requestDto, 1L, 1L,1L);
//
//    //    System.out.println(response);
//        assertNotNull(response);
//     assertEquals(true,response.isEconomicParameter());
//     assertEquals(1L,response.getCurrency().getCurrencyId());
//     assertEquals(50,response.getMinInputCostOfCrop());
//    }
//
//    @Test
//    void testCreateEconomicParameters_ProjectNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.createEconomicParameters(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testCreateEconomicParameters_CurrencyNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(currencyRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.createEconomicParameters(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testCreateEconomicParameters_UserNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.createEconomicParameters(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testCreateEconomicParameters_AlreadyExists() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(economicParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT)).thenReturn(Optional.of(economicParameters));
//
//        assertThrows(ConflictException.class, () -> {
//            economicParametersService.createEconomicParameters(requestDto, 1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testGetEconomicParameters_Success() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(economicParameterRepo.findByProjectAndStatus(project, PreProcessorStatus.DRAFT)).thenReturn(Optional.of(economicParameters));
//        when(economicModelMapper.economicToEconomicResponse(any(EconomicParameters.class))).thenReturn(economicParametersResponseDto);
//
//        EconomicParametersResponseDto response = economicParametersService.getEconomicParameters(1L, 1L);
//
//        assertNotNull(response);
//        assertEquals(true,response.isEconomicParameter());
//        assertEquals(1L,response.getCurrency().getCurrencyId());
//        assertEquals(50,response.getMinInputCostOfCrop());
//    }
//
//    @Test
//    void testGetEconomicParameters_UserNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.getEconomicParameters(1L, 1L);
//        });
//    }
//
//    @Test
//    void testGetEconomicParameters_ProjectNotFound() {
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(projectRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.getEconomicParameters(1L, 1L);
//        });
//    }
//
//    @Test
//    void testGetEconomicParameters_ProjectMismatch() {
//        UserProfile anotherUserProfile = new UserProfile();
//        anotherUserProfile.setUserProfileId(2L);
//        Projects anotherProject = new Projects();
//        anotherProject.setProjectId(2L);
//        anotherProject.setUserProfile(anotherUserProfile);
//
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(anotherProject));
//
//        assertThrows(UnprocessableException.class, () -> {
//            economicParametersService.getEconomicParameters(1L, 1L);
//        });
//    }
//
//
//   @Test
//    void testUpdateEconomicParameters_Success() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(economicParameterRepo.findById(1L)).thenReturn(Optional.of(economicParameters));
//        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(economicParameterRepo.save(any(EconomicParameters.class))).thenReturn(economicParameters);
//        when(economicModelMapper.economicToEconomicResponse(any(EconomicParameters.class))).thenReturn(economicParametersResponseDto);
//
//        EconomicParametersResponseDto response = economicParametersService.updateEconomicParameters( 1L,requestDto ,1L, 1L, 1L);
//
//        assertNotNull(response);
//        assertEquals(1L, response.getCurrency().getCurrencyId());
//    }
//
//    @Test
//    void testUpdateEconomicParameters_ProjectNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.updateEconomicParameters( 1L,requestDto, 1L, 1L, 1L);
//        });
//    }
//
////    @Test
////    void testUpdateEconomicParameters_CurrencyNotFound() {
////        Currency currency=new Currency();
////        currency.setCurrencyId(2L);
////        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
////        when(currencyRepo.findById(1L)).thenReturn(Optional.empty());
////
////        assertThrows(ResourceNotFoundException.class, () -> {
////            economicParametersService.updateEconomicParameters( 1L, requestDto, 1L, 1L);
////        });
////    }
//////
//    @Test
//    void testUpdateEconomicParameters_UserNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
////        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.updateEconomicParameters(1L, requestDto, 1L, 1L, 1L);
//        });
//   }
//
//    @Test
//    void testUpdateEconomicParameters_EconomicParametersNotFound() {
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(project));
////        when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//        when(economicParameterRepo.findById(1L)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> {
//            economicParametersService.updateEconomicParameters(1L, requestDto,1L, 1L, 1L);
//        });
//    }
//
//    @Test
//    void testUpdateEconomicParameters_ProjectMismatch() {
//        UserProfile anotherUserProfile = new UserProfile();
//        anotherUserProfile.setUserProfileId(2L);
//        Projects anotherProject = new Projects();
//        anotherProject.setProjectId(2L);
//        anotherProject.setUserProfile(anotherUserProfile);
//
//        when(projectRepo.findById(1L)).thenReturn(Optional.of(anotherProject));
//  //      when(currencyRepo.findById(1L)).thenReturn(Optional.of(currency));
//        when(userProfileRepo.findByUserId(1L)).thenReturn(Optional.of(userProfile));
//  //      when(economicParameterRepo.findById(1L)).thenReturn(Optional.of(economicParameters));
//
//        assertThrows(UnprocessableException.class, () -> {
//            economicParametersService.updateEconomicParameters( 1L, requestDto,1L, 1L, 1L);
//        });
//    }
//
//
//
//
//
//}
