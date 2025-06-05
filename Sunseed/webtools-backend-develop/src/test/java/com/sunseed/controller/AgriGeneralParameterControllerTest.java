//package com.sunseed.controller;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyBoolean;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Stream;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunseed.controller.agriGeneralParameters.AgriGeneralParametersController;
//import com.sunseed.exceptions.GlobalExceptionHandler;
//import com.sunseed.model.requestDTO.AgriGeneralParameterRequestDto;
//import com.sunseed.model.requestDTO.AgriGeneralParametersRequestDto;
//import com.sunseed.model.requestDTO.AgriPvProtectionHeightRequestDto;
//import com.sunseed.model.responseDTO.AgriGeneralParameterResponseDto;
//import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
//import com.sunseed.model.responseDTO.AgriPvProtectionHeightResponseDto;
//import com.sunseed.model.responseDTO.BedParameterResponseDto;
//import com.sunseed.response.ApiResponse;
//import com.sunseed.service.AgriGeneralParametersService;
//
//@ExtendWith(MockitoExtension.class)
//public class AgriGeneralParameterControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private AgriGeneralParametersService agriGeneralParameterService;
//
//    @Mock
//    private ApiResponse apiResponse;
//
//    @InjectMocks
//    private AgriGeneralParametersController controller;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
//        globalExceptionHandler.setApiResponse(apiResponse);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(globalExceptionHandler).build();
//
//
//    }
//
//    @Test
//    void testAddAgriGeneralParameters_ValidInput_ReturnsCreated() throws Exception {
//       //mock agri pv protection height request
//        AgriPvProtectionHeightRequestDto mockAgriPvProtectionHeightRequest = AgriPvProtectionHeightRequestDto.builder().protectionId(1L).height(10).agriPvProtectionHeightId(1L).Deleted(false).build();
//        List<AgriPvProtectionHeightRequestDto> mockRequestList = new ArrayList<>();
//        mockRequestList.add(mockAgriPvProtectionHeightRequest);
//
//        //mocking request dto
//        AgriGeneralParametersRequestDto request = AgriGeneralParametersRequestDto.builder().irrigationTypeId(1L).bedcc(1.5).bedAngle(100).bedHeight(0.5).bedWidth(4.5).bedAzimuth(250).isMulching(true).soilId(1L).maxTemp(90).minTemp(40).tempControl("trail min max").trail(20).agriPvProtectionHeight(mockRequestList) .build();
//
//        // Convert requestDto to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        BedParameterResponseDto bedParameters = BedParameterResponseDto.builder().bedcc(1.5).bedAngle(100).bedHeight(0.5).bedWidth(4.5).bedAzimuth(250).id(1L).build();
//        AgriPvProtectionHeightResponseDto agriPvProtectionHeightMockResponse = AgriPvProtectionHeightResponseDto.builder().height(10).agriPvProtectionHeightId(1L).protectionId(1L).build();
//        List<AgriPvProtectionHeightResponseDto> list = new ArrayList<>();
//        list.add(agriPvProtectionHeightMockResponse);
//        AgriGeneralParametersResponseDto response = AgriGeneralParametersResponseDto.builder().
//                irrigationType(1L).isMulching(true).soilId(1L).maxTemp(10).minTemp(8).tempControl("trail min max").trail(13).bedParameter(bedParameters).agriPvProtectionHeight(list).build();
//
//        // Mocking service method
//        when(agriGeneralParameterService.addAgriGeneralParameters(any(), anyLong(), anyLong(),anyLong())).thenReturn(response);
//
//        // Prepare response for controller method
//        Map<String, Object> responseMock = new HashMap<>();
//        responseMock.put("success", true);
//        responseMock.put("message", "agriGeneral Parameters created successfully");
//        responseMock.put("httpStatus", 201);
//        responseMock.put("data", response);
//
//        // Mocking API response
//        when(apiResponse.ResponseHandler(anyBoolean(), anyString(), any(), any())).thenReturn(new ResponseEntity<>(responseMock, HttpStatus.CREATED));
//
//        // Mocking user ID in request attribute
//        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
//        httpRequest.setAttribute("userId", 123L);
//
//        // Performing the POST request
//        //  mockMvc.perform(post("/v1/project/1/agriGeneralParameters").content(requestBody)).contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isCreated());
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/v1/project/1/agriGeneralParameters").content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isCreated());
//
//        // Verifying that the service method is called
//        verify(agriGeneralParameterService, times(1)).saveAgriGeneralParameter(any(), anyLong(), anyLong());
//    }
//
//// update test
//
//    @Test
//    void testUpdateAgriGeneralParameters_ValidInput_ReturnsOk() throws Exception {
//        //mocking agriGeneralParameter Request Dto
//        AgriPvProtectionHeightRequestDto mockAgriPvProtectionHeightRequest = AgriPvProtectionHeightRequestDto.builder().protectionId(1L).height(10).agriPvProtectionHeightId(1L).Deleted(false).build();
//        List<AgriPvProtectionHeightRequestDto> mockRequestList = new ArrayList<>();
//        mockRequestList.add(mockAgriPvProtectionHeightRequest);
//
//        //mocking request dto
//        AgriGeneralParameterRequestDto request = AgriGeneralParameterRequestDto.builder().irrigationTypeId(1L).bedcc(1.5).bedAngle(100).bedHeight(0.5).bedWidth(4.5).bedAzimuth(250).isMulching(true).soilId(1L).maxTemp(90).minTemp(40).tempControl("trail min max").trail(20).agriPvProtectionHeight(mockRequestList) .build();
//
//        // Convert requestDto to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        BedParameterResponseDto bedParameters = BedParameterResponseDto.builder().bedcc(1.5).bedAngle(100).bedHeight(0.5).bedWidth(4.5).bedAzimuth(250).id(1L).build();
//        AgriPvProtectionHeightResponseDto agriPvProtectionHeightMockResponse = AgriPvProtectionHeightResponseDto.builder().height(10).AgriPvProtectionHeightId(1L).protectionId(1L).build();
//        List<AgriPvProtectionHeightResponseDto> list = new ArrayList<>();
//        list.add(agriPvProtectionHeightMockResponse);
//        AgriGeneralParameterResponseDto response = AgriGeneralParameterResponseDto.builder().
//                irrigationType(1L).isMulching(true).soilId(1L).maxTemp(10).minTemp(8).tempControl("trail min max").trail(13).bedParameter(bedParameters).agriPvProtectionHeight(list).build();
//
//        // Mocking service method
//        when(agriGeneralParameterService.updateAgriGeneralParameter(any(), anyLong(), anyLong(), anyLong())).thenReturn(response);
//
//        // Prepare response for controller method
//        Map<String, Object> responseMock = new HashMap<>();
//        responseMock.put("success", true);
//        responseMock.put("message", "agriGeneral Parameters updated successfully");
//        responseMock.put("httpStatus", 200);
//        responseMock.put("data", response);
//
//        // Mocking API response
//        when(apiResponse.ResponseHandler(anyBoolean(), anyString(), any(), any())).thenReturn(new ResponseEntity<>(responseMock, HttpStatus.OK));
//
//        // Mocking user ID in request attribute
//        MockHttpServletRequest httpRequest = new MockHttpServletRequest();
//        httpRequest.setAttribute("userId", 123L);
//
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/v1/project/1/agriGeneralParameters/1").content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isOk());
//
//        // Verifying that the service method is called
//        verify(agriGeneralParameterService, times(1)).updateAgriGeneralParameter(any(), anyLong(), anyLong(), anyLong());
//    }
//
//    //  request dto bad request test  for add general parameters
//
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestData")
//    void testAddAgriGeneralParameters_InvalidInput_ReturnsBadRequest(AgriGeneralParameterRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
//     //   when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//        when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(post("/v1/project/1/agriGeneralParameters")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//    // test request dto bad request for update agri general parameters
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestData")
//    void testUpdateAgriGeneralParameters_InvalidInput_ReturnsBadRequest(AgriGeneralParameterRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
////        when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//        when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(put("/v1/project/1/agriGeneralParameters/1")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//// test cases for control checks when add agrigeneral parameters
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestDataForController")
//    void testAddAgriGeneralParameters_Controller_ReturnsBadRequest(AgriGeneralParameterRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
//           when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//       // when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(post("/v1/project/1/agriGeneralParameters")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//    // test case for controller level checks when update agrigeneral parameters
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestDataForController")
//    void testUpdateAgriGeneralParameters_Controller_ReturnsBadRequest(AgriGeneralParameterRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
//        when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
// //       when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(put("/v1/project/1/agriGeneralParameters/1")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//
//    static Stream<AgriGeneralParameterRequestDto> provideInvalidRequestData() {
//        // Providing invalid request data with missing fields for TempControl.TRAIL_MIN_MAX and TempControl.ABSOLUTE_MIN_MAX
//        AgriPvProtectionHeightRequestDto mockAgriPvProtectionHeightRequest = AgriPvProtectionHeightRequestDto.builder().protectionId(1L).height(10).agriPvProtectionHeightId(1L).Deleted(false).build();
//        List<AgriPvProtectionHeightRequestDto> mockRequestList = new ArrayList<>();
//        mockRequestList.add(mockAgriPvProtectionHeightRequest);
//        return Stream.of(
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(null) // null agriPvProtection height
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .trail(30)
//                        .minTemp(30)
//                        .maxTemp(50)
//                        .tempControl("trail min max") // Missing 'trail', 'minTemp', 'maxTemp' fields
//                        .build(),
//
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .maxTemp(10)
//                        .minTemp(5)
//                        .trail(150) //trail value is greater than 50
//                        .tempControl("trail min max")
//                        .build(),
//
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(200) //min temp is greater than 100
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(200) //max temp is greater than 100
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(200) //bed angle greater than 180
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(25.5) // bedcc is greater than 20
//                        .bedAngle(30)
//                        .bedHeight(0.2)
//                        .bedWidth(4.0)
//                        .bedAzimuth(25)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(2.0) // bed height range is 0.1 to 1
//                        .bedWidth(4.0)
//                        .bedAzimuth(25)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(8.0) //bed width greater than 5
//                        .bedAzimuth(25)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(400) //bed azimuth greater than 360
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(null) //bedcc is null
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(null) //bed angle is null
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(100)
//                        .bedHeight(null) //bed height is null
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.6)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(null) //bed width is null
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.6)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(null) //bed azimuth is null
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(null) //irrigation is null
//                        .bedcc(1.6)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.6)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(true)
//                        .soilId(null) //soil id is null
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .irrigationTypeId(1L)
//                        .bedcc(1.6)
//                        .bedAngle(100)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(300)
//                        .isMulching(false)
//                        .soilId(1L)
//                        .minTemp(80)
//                        .maxTemp(90)
//                        .tempControl(null) // temp control is null
//                        .build()
//
//        );
//    }
//
//
//    // methods for  controller checks data
//    static Stream<AgriGeneralParameterRequestDto> provideInvalidRequestDataForController() {
//        // Providing invalid request data with missing fields for TempControl.TRAIL_MIN_MAX and TempControl.ABSOLUTE_MIN_MAX
//        AgriPvProtectionHeightRequestDto mockAgriPvProtectionHeightRequest = AgriPvProtectionHeightRequestDto.builder().protectionId(1L).height(10).agriPvProtectionHeightId(1L).Deleted(false).build();
//        List<AgriPvProtectionHeightRequestDto> mockRequestList = new ArrayList<>();
//        mockRequestList.add(mockAgriPvProtectionHeightRequest);
//        return Stream.of(
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .tempControl("trail min max") // Missing 'trail', 'minTemp', 'maxTemp' fields
//                        .build(),
//
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .maxTemp(10)
//                        .minTemp(5)
//                        .tempControl("trail min max") // Missing 'trail' fields
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .maxTemp(10)
//                        .trail(15)
//                        .tempControl("trail min max") // Missing  'minTemp' fields
//                        .build(),
//
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(10)
//                        .trail(15)
//                        .tempControl("trail min max") // Missing  'maxTemp' fields
//                        .build(),
//
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        .minTemp(8) // Missing 'minTemp' field
//                        .tempControl("absolute min max")
//                        .build(),
//                AgriGeneralParameterRequestDto.builder()
//                        .agriPvProtectionHeight(mockRequestList)
//                        .irrigationTypeId(1L)
//                        .bedcc(1.5)
//                        .bedAngle(30)
//                        .bedHeight(0.6)
//                        .bedWidth(4.5)
//                        .bedAzimuth(250)
//                        .isMulching(true)
//                        .soilId(1L)
//                        // Missing 'maxTemp','minTemp field
//                        .tempControl("absolute min max")
//                        .build()
//
//
//        );
//    }
//
//// test get controller
//@Test
//void testGetAgriGeneralParameters_ReturnsOk() throws Exception {
//    //mock agri pv protection height request
//
//    BedParameterResponseDto bedParameters = BedParameterResponseDto.builder().bedcc(1.5).bedAngle(100).bedHeight(0.5).bedWidth(4.5).bedAzimuth(250).id(1L).build();
//    AgriPvProtectionHeightResponseDto agriPvProtectionHeightMockResponse = AgriPvProtectionHeightResponseDto.builder().height(10).AgriPvProtectionHeightId(1L).protectionId(1L).build();
//    List<AgriPvProtectionHeightResponseDto> list = new ArrayList<>();
//    list.add(agriPvProtectionHeightMockResponse);
//    AgriGeneralParameterResponseDto response = AgriGeneralParameterResponseDto.builder().
//            irrigationType(1L).isMulching(true).soilId(1L).maxTemp(10).minTemp(8).tempControl("trail min max").trail(13).bedParameter(bedParameters).agriPvProtectionHeight(list).status("DRAFT"). build();
//
//    // Mocking service method
//    when(agriGeneralParameterService.getAgriGeneralParameter(anyLong(),anyLong())).thenReturn(response);
//
//    // Prepare response for controller method
//    Map<String, Object> responseMock = new HashMap<>();
//    responseMock.put("success", true);
//    responseMock.put("message", "agriGeneral Parameters fetched successfully");
//    responseMock.put("httpStatus", 200);
//    responseMock.put("data", response);
//
//    // Mocking API response
//    when(apiResponse.ResponseHandler(anyBoolean(), anyString(), any(), any())).thenReturn(new ResponseEntity<>(responseMock, HttpStatus.OK));
//
//    // Mocking user ID in request attribute
//    MockHttpServletRequest httpRequest = new MockHttpServletRequest();
//    httpRequest.setAttribute("userId", 123L);
//
//    // Performing the POST request
//    //  mockMvc.perform(post("/v1/project/1/agriGeneralParameters").content(requestBody)).contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isCreated());
//
//    mockMvc.perform(MockMvcRequestBuilders.get("/v1/project/1/agriGeneralParameters")
//            .contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isOk());
//
//    // Verifying that the service method is called
//    verify(agriGeneralParameterService, times(1)).getAgriGeneralParameter( anyLong(), anyLong());
//}
//
//}
