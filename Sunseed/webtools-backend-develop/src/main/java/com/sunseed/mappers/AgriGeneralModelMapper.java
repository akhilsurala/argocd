package com.sunseed.mappers;

import org.springframework.stereotype.Component;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.AgriPvProtectionHeight;
import com.sunseed.model.responseDTO.AgriGeneralGetRunResponseDto;
import com.sunseed.model.responseDTO.AgriGeneralParametersResponseDto;
import com.sunseed.model.responseDTO.AgriPvProtectionHeightResponseDto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AgriGeneralModelMapper {
//    private final ModelMapper modelMapper;
//
//    public AgriGeneralParameter economicRequestToEconomic(AgriGeneralParametersRequestDto request) {
//        return modelMapper.map(request, AgriGeneralParameter.class);
//    }

//    public AgriGeneralParametersResponseDto agriGeneralToAgriGeneralResponse(AgriGeneralParameter agriGeneralParameters) {
//        return modelMapper.map(agriGeneralParameters, AgriGeneralParametersResponseDto.class);
//    }
    
    public AgriGeneralGetRunResponseDto entityToAgriGeneralParameterResponseDto(AgriGeneralParameter agriGeneralParameter) {
    	AgriGeneralGetRunResponseDto agriGeneralParameterResponseDto  = new AgriGeneralGetRunResponseDto();
//		pvParameterResponseDto.setPreProcessorToggle(toggles);
//		pvParameterResponseDto.setRunId(runId);
//		if(pvParameter == null) {
//			return pvParameterResponseDto;
//			
//		} 
		
		agriGeneralParameterResponseDto.setId(agriGeneralParameter.getId());
//		agriGeneralParameterResponseDto.setAgriPvProtectionHeight(null);
		agriGeneralParameterResponseDto.setBedParameter(agriGeneralParameter.getBedParameter());
		agriGeneralParameterResponseDto.setProjectId(agriGeneralParameter.getProject().getProjectId());
		agriGeneralParameterResponseDto.setRunId(agriGeneralParameter.getRun().getRunId());
//		agriGeneralParameterResponseDto.setSoilType(agriGeneralParameter.getSoilType());
		agriGeneralParameterResponseDto.setIrrigationType(agriGeneralParameter.getIrrigationId());
		agriGeneralParameterResponseDto.setTempControl(agriGeneralParameter.getTempControl());
		agriGeneralParameterResponseDto.setTrail(agriGeneralParameter.getTrail());
		agriGeneralParameterResponseDto.setMinTemp(agriGeneralParameter.getMinTemp());
		agriGeneralParameterResponseDto.setMaxTemp(agriGeneralParameter.getMaxTemp());
		agriGeneralParameterResponseDto.setIsMulching(agriGeneralParameter.getIsMulching());
		agriGeneralParameterResponseDto.setStatus(agriGeneralParameter.getStatus().getValue());
		List<AgriPvProtectionHeight> agriPvProtectionHeights = agriGeneralParameter.getAgriPvProtectionHeight();
		List<AgriPvProtectionHeightResponseDto > agriPvProtectionHeightDtos = agriPvProtectionHeights.stream()
				.map(agriPvProtectionHeight -> {
					AgriPvProtectionHeightResponseDto dto = new AgriPvProtectionHeightResponseDto();
					dto.setAgriPvProtectionHeightId(agriPvProtectionHeight.getId());
					dto.setHeight(agriPvProtectionHeight.getProtectionHeight());
					dto.setProtectionId(agriPvProtectionHeight.getProtectionLayer().getProtectionLayerId());
					dto.setProtectionLayerName(agriPvProtectionHeight.getProtectionLayer().getProtectionLayerName());
					return dto;
				})
				.collect(Collectors.toList());

		agriGeneralParameterResponseDto.setAgriPvProtectionHeight(agriPvProtectionHeightDtos);

		return agriGeneralParameterResponseDto;
	}
}
