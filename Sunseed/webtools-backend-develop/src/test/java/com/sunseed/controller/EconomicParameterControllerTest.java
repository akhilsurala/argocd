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
//import java.util.HashMap;
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
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.sunseed.controller.economicParameters.EconomicParametersController;
//import com.sunseed.exceptions.GlobalExceptionHandler;
//import com.sunseed.model.requestDTO.EconomicParametersRequestDto;
//import com.sunseed.model.responseDTO.CurrencyResponse;
//import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
//import com.sunseed.response.ApiResponse;
//import com.sunseed.service.EconomicParametersService;
//
//@ExtendWith(MockitoExtension.class)
//public class EconomicParameterControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private EconomicParametersService economicParameterService;
//
//    @Mock
//    private ApiResponse apiResponse;
//
//
//    @InjectMocks
//    private EconomicParametersController controller;
//
//    private ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    void setUp() {
//        GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
//        globalExceptionHandler.setApiResponse(apiResponse);
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(globalExceptionHandler).build();
//    }
//
//    @Test
//    void testAddEconomicParameters_ValidInput_ReturnsCreated() throws Exception {
//        Integer[] mockHourly = {4, 6, 5, 8, 910};
//
//        // Mocking request DTO
//        EconomicParametersRequestDto request = EconomicParametersRequestDto.builder()
//                .economicParameter(true)
//                .currencyId(1L)
//                .minInputCostOfCrop(10.0)
//                .maxInputCostOfCrop(40.0)
//                .minReferenceYieldCost(20.0)
//                .maxReferenceYieldCost(50.0)
//
//                .minSellingCostOfCrop(10.0)
//                .maxSellingCostOfCrop(35.0)
//                .hourlySellingRates(mockHourly)
//                .build();
//
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // mock currency
//        CurrencyResponse currency = new CurrencyResponse();
//        currency.setCurrencyId(1L);
//        currency.setCurrency("INR");
//
//        // Mocking response DTO
//        EconomicParametersResponseDto response = EconomicParametersResponseDto.builder()
//                .economicId(1L)
//                .currency(currency)
//                .minInputCostOfCrop(10.0)
//                .maxInputCostOfCrop(40.0)
//                .minReferenceYieldCost(20.0)
//                .maxReferenceYieldCost(50.0)
//
//                .minSellingCostOfCrop(10.0)
//                .maxSellingCostOfCrop(35.0)
//                .hourlySellingRates(mockHourly)
//                .build();
//
//                // Mocking service method
//        when(economicParameterService.createEconomicParameters(any(), anyLong(),anyLong(),anyLong())).thenReturn(response);
//
//        // Prepare response for controller method
//        Map<String, Object> responseMock = new HashMap<>();
//        responseMock.put("success", true);
//        responseMock.put("message", "Economic parameters created successfully");
//        responseMock.put("httpStatus", 201);
//        responseMock.put("data", response);
//
//        // Mocking API response
//        when(apiResponse.ResponseHandler(anyBoolean(), anyString(), any(), any())).thenReturn(new ResponseEntity<>(responseMock, HttpStatus.CREATED));
//
//        // Performing the POST request
//        mockMvc.perform(post("/v1/project/1/economicParameters").content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isCreated());
//
//        // Verifying that the service method is called
//        verify(economicParameterService, times(1)).createEconomicParameters(any(), anyLong(),anyLong(),anyLong());
//    }
//
//    @Test
//    void testUpdateEconomicParameters_ValidInput_ReturnsOk() throws Exception {
//        Integer[] mockHourly = {4, 6, 5, 8, 910};
//
//        // Mocking request DTO
//        EconomicParametersRequestDto request = EconomicParametersRequestDto.builder()
//                .economicParameter(true)
//                .currencyId(1L)
//                .minInputCostOfCrop(10.0)
//                .maxInputCostOfCrop(40.0)
//                .minReferenceYieldCost(20.0)
//                .maxReferenceYieldCost(50.0)
//                .minSellingCostOfCrop(10.0)
//                .maxSellingCostOfCrop(35.0)
//                .hourlySellingRates(mockHourly)
//                .build();
//
//
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        // mock currency
//        CurrencyResponse currency = new CurrencyResponse();
//        currency.setCurrencyId(1L);
//        currency.setCurrency("INR");
//        // Mocking response DTO
//        EconomicParametersResponseDto response = EconomicParametersResponseDto.builder()
//                .economicId(1L)
//                .currency(currency)
//                .minInputCostOfCrop(10.0)
//                .maxInputCostOfCrop(40.0)
//                .minReferenceYieldCost(20.0)
//                .maxReferenceYieldCost(50.0)
//
//                .minSellingCostOfCrop(10.0)
//                .maxSellingCostOfCrop(35.0)
//                .hourlySellingRates(mockHourly)
//                .build();
//
//        // Mocking service method
//        when(economicParameterService.updateEconomicParameters(anyLong(), any(), anyLong(),anyLong(), anyLong())).thenReturn(response);
//
//        // Prepare response for controller method
//        Map<String, Object> responseMock = new HashMap<>();
//        responseMock.put("success", true);
//        responseMock.put("message", "Economic parameters updated successfully");
//        responseMock.put("httpStatus", 200);
//        responseMock.put("data", response);
//
//        // Mocking API response
//        when(apiResponse.ResponseHandler(anyBoolean(), anyString(), any(), any())).thenReturn(new ResponseEntity<>(responseMock, HttpStatus.OK));
//
//        // Performing the PUT request
//        mockMvc.perform(put("/v1/project/1/economicParameters/1").content(requestBody)
//                .contentType(MediaType.APPLICATION_JSON).requestAttr("userId", 123L)).andExpect(status().isOk());
//
//        // Verifying that the service method is called
//        verify(economicParameterService, times(1)).updateEconomicParameters(anyLong(), any(), anyLong(),anyLong(), anyLong());
//    }
//
//  @ParameterizedTest
//  @MethodSource("provideInvalidRequestData")
//    void testAddEconomicParameters_InvalidInput_ReturnsBadRequest(EconomicParametersRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//
//        // Mocking API response
//        when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(post("/v1/project/1/economicParameters")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//      @ParameterizedTest
//    @MethodSource("provideInvalidRequestData")
//    void testUpdateEconomicParameters_InvalidInput_ReturnsBadRequest(EconomicParametersRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "all field is required");
//
//        // Mocking API response
//        when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(), eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the PUT request
//        mockMvc.perform(put("/v1/project/1/economicParameters/1")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestDataForController")
//    void testAddEconomicParameters_Controller_ReturnsBadRequest(EconomicParametersRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "min value must be less than max value");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
//        when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//        // when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(post("/v1/project/1/economicParameters")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//    // test case for controller level checks when update agrigeneral parameters
//    @ParameterizedTest
//    @MethodSource("provideInvalidRequestDataForController")
//    void testUpdateEconomicParameters_Controller_ReturnsBadRequest(EconomicParametersRequestDto request) throws Exception {
//        // Convert request DTO to JSON string
//        String requestBody = objectMapper.writeValueAsString(request);
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("httpStatus", HttpStatus.BAD_REQUEST);
//        response.put("message", "min value must be less than max value");
//        //  return new ResponseEntity<>(response, httpStatus);
//
//        // Mocking API response
//        when(apiResponse.errorHandler(any(), anyString())).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//        //       when(apiResponse.responseHandlerForMethodArgumentNotValidException(any(), any(),eq(HttpStatus.BAD_REQUEST))).thenReturn(new ResponseEntity<>(response, HttpStatus.BAD_REQUEST));
//
//        // Performing the POST request
//        mockMvc.perform(put("/v1/project/1/economicParameters/1")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .requestAttr("userId", 123L))
//                .andExpect(status().isBadRequest());
//    }
//
//
//
//
//
//    static Stream<EconomicParametersRequestDto> provideInvalidRequestData() {
//        Integer[] mockHourly = {4, 6, 5, 8, 910};
//        return Stream.of(
//                // Mocking request DTO
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(-2.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(-4.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(-2.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(-5.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//
//
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(-1.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(-3.0)
//                        .hourlySellingRates(mockHourly)
//                        .build()
//
//
//
//
//       );
//    }
//    static Stream<EconomicParametersRequestDto> provideInvalidRequestDataForController() {
//        Integer[] mockHourly = {4, 6, 5, 8, 910};
//        return Stream.of(
//                // Mocking request DTO
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(50.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(10.0) //max<min
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(10.0) //max<min
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(60.0) //min>max
//                        .maxReferenceYieldCost(50.0)
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//                        .minSellingCostOfCrop(50.0) //min>max
//                        .maxSellingCostOfCrop(35.0)
//                        .hourlySellingRates(mockHourly)
//                        .build(),
//                EconomicParametersRequestDto.builder()
//                        .economicParameter(true)
//                        .currencyId(1L)
//                        .minInputCostOfCrop(20.0)
//                        .maxInputCostOfCrop(40.0)
//                        .minReferenceYieldCost(20.0)
//                        .maxReferenceYieldCost(50.0)
//                        .minSellingCostOfCrop(10.0)
//                        .maxSellingCostOfCrop(5.0) //max<min
//                        .hourlySellingRates(mockHourly)
//                        .build()
//        );
//    }
//}
