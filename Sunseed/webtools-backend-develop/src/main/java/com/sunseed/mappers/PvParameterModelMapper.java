package com.sunseed.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.entity.PvParameter;
import com.sunseed.model.responseDTO.PvParametersResponseDto;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PvParameterModelMapper {
	
	@Autowired
//	@Qualifier("pvParametermodelMapper")
	private ModelMapper modelMapper;
	
	public PvParametersResponseDto entityToPvParameterResponseDto(PvParameter pvParameter, PreProcessorToggle toggles,
			Long runId) {
		PvParametersResponseDto pvParameterResponseDto  = new PvParametersResponseDto();
		pvParameterResponseDto.setPreProcessorToggle(toggles);
		pvParameterResponseDto.setRunId(runId);
		if(pvParameter == null) {
			return pvParameterResponseDto;
			
		} 
		
		pvParameterResponseDto.setId(pvParameter.getId());
		pvParameterResponseDto.setProjectId(pvParameter.getProject().getProjectId());
		pvParameterResponseDto.setTiltIfFt(pvParameter.getTiltIfFt());
		pvParameterResponseDto.setMaxAngleOfTracking(pvParameter.getMaxAngleOfTracking());
		pvParameterResponseDto.setModuleMaskPattern(pvParameter.getModuleMaskPattern());
		pvParameterResponseDto.setGapBetweenModules(pvParameter.getGapBetweenModules());
		pvParameterResponseDto.setHeight(pvParameter.getHeight());
//		pvParameterResponseDto.setPitchOfRows(pvParameter.getPitchOfRows());
//		pvParameterResponseDto.setAzimuth(pvParameter.getAzimuth());
//		pvParameterResponseDto.setLengthOfOneRow(pvParameter.getLengthOfOneRow());
		pvParameterResponseDto.setStatus(pvParameter.getStatus());
		pvParameterResponseDto.setPvModule(pvParameter.getPvModule());
		pvParameterResponseDto.setModeOfOperationId(pvParameter.getModeOfOperationId());
		pvParameterResponseDto.setModuleConfigs(pvParameter.getModuleConfig());
//		pvParameterResponseDto.setPreProcessorToggles(toggles);

		return pvParameterResponseDto;
	}


}
