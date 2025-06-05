package com.sunseed.controller.PostProcessing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sunseed.model.requestDTO.HourlyDetailsPayload;
import com.sunseed.model.responseDTO.HourlyDetailsResponse;
import com.sunseed.model.responseDTO.PostProcessingDetailsResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.PostprocessingDetailsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class PostprocessingDetailsController {


    private final PostprocessingDetailsService postProcessingService;
    private final ApiResponse apiResponse;


    //frequency= hourly/weekly , datatype= across/within run , quantity=
    @PutMapping("/project/{projectId}/hourly-details")
    public ResponseEntity<Object> gethourlyDetails(@RequestBody HourlyDetailsPayload request, @RequestParam(value = "quantity") String quantity, @RequestParam(value = "dataType") String dataType, @RequestParam(value = "frequency") String frequency, @PathVariable(name = "projectId") Long projectId, HttpServletRequest httpRequest) throws Exception {
        Long userId = (Long) httpRequest.getAttribute("userId");
        System.out.println("print quantity in controller : " + quantity);
        List<?> postProcessingDetailsResponse = postProcessingService.getHourlyDetails(request, quantity, dataType, frequency, projectId, userId);
        return apiResponse.ResponseHandler(true, "details.fetched", HttpStatus.OK, postProcessingDetailsResponse);
    }

    @PutMapping("/project/{projectId}/postprocessing-details")
    public ResponseEntity<Object> getPostprocessingDetails(@RequestBody Map<String, List<Long>> json, HttpServletRequest request, @PathVariable(name = "projectId") Long projectId, @RequestParam(value = "dataType") String dataType, @RequestParam(value = "frequency") String frequency) {
        List<Long> runIds = json.get("runIds");
        Long userId = (Long) request.getAttribute("userId");
        Map<String, List<PostProcessingDetailsResponseDto>> postProcessingDetailsResponse = postProcessingService.getPostprocessingDetails(runIds, userId, projectId, dataType, frequency);
        return apiResponse.ResponseHandler(true, "details.fetched", HttpStatus.OK, postProcessingDetailsResponse);

    }

}
