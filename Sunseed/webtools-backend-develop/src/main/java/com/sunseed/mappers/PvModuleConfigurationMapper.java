package com.sunseed.mappers;

import org.springframework.stereotype.Component;

import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.model.responseDTO.pvParameters.PvModuleConfigurationResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PvModuleConfigurationMapper {
	
	
	public PvModuleConfigurationResponse entityToResponse(PvModuleConfiguration entity) 
	{
		PvModuleConfigurationResponse response = new PvModuleConfigurationResponse();
		response.setId(entity.getId());
		response.setOrdering(entity.getOrdering());
		response.setName(entity.getModuleConfig());
		response.setNumberOfModules(entity.getNumberOfModules());
		response.setTypeOfModule(entity.getTypeOfModule().getValue());
		response.setHide(entity.getHide());
		return response;
		
	}
	

}
