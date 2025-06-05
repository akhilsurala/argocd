package com.sunseed.controller.cropParameters;

import io.micrometer.common.lang.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sunseed.model.requestDTO.CropParameterRequestDto;
import com.sunseed.model.responseDTO.CropParametersResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.CropParameterService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/project")
public class CropParameterController {

    private final ApiResponse apiResponse;
    private final CropParameterService cropParameterService;

    @PostMapping("/{projectId}/cropParameters")
    public ResponseEntity<Object> addCropParameters(@RequestBody CropParameterRequestDto request,
                                                    @PathVariable Long projectId, @Nullable @RequestParam Long runId, HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        System.out.println("Creating crop parameters !" + request);
        CropParametersResponseDto response = cropParameterService.saveCropParameter(request, projectId, runId, userId);

        return apiResponse.ResponseHandler(true, "cropParameters.created", HttpStatus.CREATED, response);
    }

    @GetMapping("/{projectId}/cropParameters")
    public ResponseEntity<Object> getCropParameters(@PathVariable Long projectId, @Nullable @RequestParam Long runId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        System.out.println(userId + " userId ");
        CropParametersResponseDto response = cropParameterService.getCropParameters(projectId, userId, runId);

        return apiResponse.ResponseHandler(true, "cropParameters.fetched", HttpStatus.OK, response);
    }

    @PutMapping("/{projectId}/cropParameters/{cropParameterId}")
    public ResponseEntity<Object> updatedCropParameters(@RequestBody CropParameterRequestDto request,
                                                        @PathVariable Long projectId, @PathVariable Long cropParameterId, @Nullable @RequestParam Long runId,
                                                        HttpServletRequest httpRequest) {
        Long userId = (Long) httpRequest.getAttribute("userId");
        System.out.println(" crop parameters request dto :" + request);
        CropParametersResponseDto response = cropParameterService.updateCropParameter(request,
                projectId, cropParameterId, runId, userId);

        return apiResponse.ResponseHandler(true, "cropParameter.updated", HttpStatus.OK, response);
    }
}