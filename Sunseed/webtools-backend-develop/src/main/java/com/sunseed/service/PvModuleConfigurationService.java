package com.sunseed.service;

import java.util.List;

import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.model.requestDTO.masterTables.PvModuleConfigurationRequestDto;
import com.sunseed.model.responseDTO.pvParameters.PvModuleConfigurationResponse;

public interface PvModuleConfigurationService {

	List<PvModuleConfigurationResponse> getPvModuleConfigurations();

	List<PvModuleConfiguration> getActivePvModuleConfigurations();

	List<PvModuleConfigurationResponse> getPvModuleConfigurations(String search);

	PvModuleConfigurationResponse getPvModuleConfigurationById(Long pvModuleConfigurationId);

	PvModuleConfigurationResponse addPvModuleConfiguration(PvModuleConfigurationRequestDto requestDto);

	PvModuleConfigurationResponse updatePvModuleConfiguration(PvModuleConfigurationRequestDto requestDto,
			Long pvModuleConfigurationId);

	void deletePvModuleConfiguration(Long pvModuleConfigurationId);

}
