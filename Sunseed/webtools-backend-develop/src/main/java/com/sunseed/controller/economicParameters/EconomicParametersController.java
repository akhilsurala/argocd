package com.sunseed.controller.economicParameters;

import com.sunseed.exceptions.AgriGeneralParametersException;
import com.sunseed.model.ValidationGroups;
import com.sunseed.model.requestDTO.AgriGeneralParametersRequestDto;
import jakarta.validation.ConstraintViolation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.model.requestDTO.EconomicParametersRequestDto;
import com.sunseed.model.responseDTO.EconomicParametersResponseDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.EconomicParametersService;

import io.micrometer.common.lang.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Set;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/project")
public class EconomicParametersController {

    private final EconomicParametersService economicParametersService;
    private final ApiResponse apiResponse;
    @PostMapping("/{projectId}/economicParameters")
    public ResponseEntity<Object> addEconomicParameters(@RequestBody EconomicParametersRequestDto request, @PathVariable Long projectId, HttpServletRequest httpRequest
            , @Nullable @RequestParam Long runId) {

      //  System.out.println(request.getCurrencyId() + " currency id");
        Long userId = (Long) httpRequest.getAttribute("userId");
        EconomicParametersResponseDto economicResponse = economicParametersService.createEconomicParameters(request, projectId, userId, runId);
        return apiResponse.ResponseHandler(true, "EconomicParameter.created", HttpStatus.CREATED, economicResponse);
    }

    @GetMapping("/{projectId}/economicParameters")
    public ResponseEntity<Object> getEconomicParameter(@PathVariable Long projectId, HttpServletRequest request,@Nullable @RequestParam Long runId) {
        Long userId = (Long) request.getAttribute("userId");
        System.out.println(userId + " userId ");
        EconomicParametersResponseDto response = economicParametersService.getEconomicParameters(projectId,
                userId,runId);

        return apiResponse.ResponseHandler(true, "economicParameter.fetched", HttpStatus.OK, response);
    }

    @PutMapping("/{projectId}/economicParameters/{economicParameterId}")

    public ResponseEntity<Object> updateEconomicParameters(@PathVariable Long projectId, @RequestBody EconomicParametersRequestDto request, @PathVariable Long economicParameterId,HttpServletRequest httpRequest,@Nullable  @RequestParam Long runId) {

        Long userId = (Long) httpRequest.getAttribute("userId");
        System.out.println(userId + " userId ");

        EconomicParametersResponseDto response = economicParametersService.updateEconomicParameters(projectId, request, economicParameterId,userId, runId);


        return apiResponse.ResponseHandler(true, "economicParameters.updated", HttpStatus.OK, response);
    }
}
