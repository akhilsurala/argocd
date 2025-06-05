package com.sunseed.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sunseed.entity.PvModuleConfiguration;
import com.sunseed.enums.PVModuleConfigType;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.mappers.PvModuleConfigurationMapper;
import com.sunseed.model.requestDTO.masterTables.PvModuleConfigurationRequestDto;
import com.sunseed.model.responseDTO.pvParameters.PvModuleConfigurationResponse;
import com.sunseed.repository.PvModuleConfigurationRepository;
import com.sunseed.service.PvModuleConfigurationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PvModuleConfigurationServiceImpl implements PvModuleConfigurationService {

	private final PvModuleConfigurationRepository moduleConfigurationRepository;

	private final PvModuleConfigurationMapper pvModuleConfigurationMapper;

	@Override
	public List<PvModuleConfigurationResponse> getPvModuleConfigurations() {
		List<PvModuleConfiguration> configurations = moduleConfigurationRepository.findAllByOrderByOrderingAsc();

		List<PvModuleConfigurationResponse> moduleConfigurations = configurations.stream()
				.map(moduleConfiguration -> PvModuleConfigurationResponse.builder().id(moduleConfiguration.getId())
						.name(moduleConfiguration.getModuleConfig()).ordering(moduleConfiguration.getOrdering())
						.numberOfModules(moduleConfiguration.getNumberOfModules())
						.typeOfModule(moduleConfiguration.getTypeOfModule().getValue())
						.createdAt(moduleConfiguration.getCreatedAt()).updatedAt(moduleConfiguration.getUpdatedAt())
						.hide(moduleConfiguration.getHide()).build())
				.collect(Collectors.toList());

		return moduleConfigurations;
	}
	
	@Override
	public List<PvModuleConfigurationResponse> getPvModuleConfigurations(String searchString) {
		 List<PvModuleConfiguration> configurations;

		    if (searchString == null || searchString.trim().isEmpty()) {
		      configurations = moduleConfigurationRepository.findAllByOrderByOrderingAsc();
		    }

		    else {
		      configurations = moduleConfigurationRepository.findAllBySearchOrderByOrderingAsc(searchString);
		    }

		    List<PvModuleConfigurationResponse> moduleConfigurations = configurations.stream()
		        .map(moduleConfiguration -> PvModuleConfigurationResponse.builder().id(moduleConfiguration.getId())
		            .name(moduleConfiguration.getModuleConfig()).ordering(moduleConfiguration.getOrdering())
		            .numberOfModules(moduleConfiguration.getNumberOfModules())
		            .typeOfModule(moduleConfiguration.getTypeOfModule().getValue())
		            .createdAt(moduleConfiguration.getCreatedAt()).updatedAt(moduleConfiguration.getUpdatedAt())
		            .hide(moduleConfiguration.getHide()).build())
		        .collect(Collectors.toList());

		    return moduleConfigurations;

	}


	@Override
	public List<PvModuleConfiguration> getActivePvModuleConfigurations() {
		List<PvModuleConfiguration> configurations = moduleConfigurationRepository
				.findByIsActiveTrueAndHideFalseOrderByOrderingAsc();
		return configurations;
	}

	@Override
	public PvModuleConfigurationResponse getPvModuleConfigurationById(Long pvModuleConfigurationId) {
		if (pvModuleConfigurationId == null || pvModuleConfigurationId <= 0)
			throw new UnprocessableException("pvModuleConfiguration.not.found");
		Optional<PvModuleConfiguration> pvModuleConfiguration = moduleConfigurationRepository
				.findByIdAndIsActiveTrue(pvModuleConfigurationId);
		if (pvModuleConfiguration.isEmpty())
			throw new UnprocessableException("pvModuleConfiguration.not.found");
		PvModuleConfiguration pvModuleConfig = pvModuleConfiguration.get();

		PvModuleConfigurationResponse response = this.pvModuleConfigurationMapper.entityToResponse(pvModuleConfig);

		return response;

	}

	@Override
	public PvModuleConfigurationResponse addPvModuleConfiguration(PvModuleConfigurationRequestDto requestDto) {
		Optional<PvModuleConfiguration> optionalPvModuleConfiguration = moduleConfigurationRepository
				.findByModuleConfigIgnoreCase(requestDto.getName());
		if (optionalPvModuleConfiguration.isPresent())
			throw new ConflictException("pvModuleConfiguration.exists");

		if (moduleConfigurationRepository.existsByOrdering(requestDto.getOrdering())) {
			throw new ConflictException("ordering.already.exists");
		}

		if (!(requestDto.getOrdering() <= (moduleConfigurationRepository.count() + 1))) {

			// to be added later
		}

		PvModuleConfiguration newPvModuleConfiguration = PvModuleConfiguration.builder()
				.moduleConfig(requestDto.getName()).numberOfModules(requestDto.getNumberOfModules())
				.ordering(requestDto.getOrdering())
				.typeOfModule(PVModuleConfigType.fromString(requestDto.getTypeOfModule())).build();

		PvModuleConfiguration savedPvModuleConfiguration = moduleConfigurationRepository.save(newPvModuleConfiguration);

		PvModuleConfigurationResponse response = this.pvModuleConfigurationMapper
				.entityToResponse(savedPvModuleConfiguration);

		return response;

	}

	@Override
	public PvModuleConfigurationResponse updatePvModuleConfiguration(PvModuleConfigurationRequestDto requestDto,
			Long pvModuleConfigurationId) {
		if (pvModuleConfigurationId == null || pvModuleConfigurationId <= 0)
			throw new UnprocessableException("pvModuleConfiguration.not.found");
		Optional<PvModuleConfiguration> optionalPvModuleConfiguration = moduleConfigurationRepository
				.findById(pvModuleConfigurationId);
		if (optionalPvModuleConfiguration.isEmpty())
			throw new UnprocessableException("pvModuleConfiguration.not.found");
		Long existingPvModuleConfigurationId = moduleConfigurationRepository
				.findIdWithModuleConfigIgnoreCase(requestDto.getName());
		if (existingPvModuleConfigurationId != null && existingPvModuleConfigurationId != pvModuleConfigurationId)
			throw new UnprocessableException("pvModuleConfiguration.exists");

		if (moduleConfigurationRepository.existsByOrderingAndIdNot(requestDto.getOrdering(), pvModuleConfigurationId)) {
			throw new ConflictException("ordering.already.exists");
		}

		PvModuleConfiguration existingPvModuleConfiguration = optionalPvModuleConfiguration.get();
		existingPvModuleConfiguration.setModuleConfig(requestDto.getName());

		existingPvModuleConfiguration.setTypeOfModule(PVModuleConfigType.fromString(requestDto.getTypeOfModule()));
		existingPvModuleConfiguration.setOrdering(requestDto.getOrdering());
		existingPvModuleConfiguration.setHide(requestDto.getHide());

		PvModuleConfiguration updatedPvModuleConfiguration = moduleConfigurationRepository
				.save(existingPvModuleConfiguration);

		PvModuleConfigurationResponse response = this.pvModuleConfigurationMapper
				.entityToResponse(updatedPvModuleConfiguration);

		return response;
	}

	@Override
	public void deletePvModuleConfiguration(Long pvModuleConfigurationId) {

		if (pvModuleConfigurationId == null || pvModuleConfigurationId <= 0)
			throw new UnprocessableException("pvModuleConfiguration.not.found");

		// performing soft delete
		PvModuleConfiguration pvModuleConfiguration = moduleConfigurationRepository.findById(pvModuleConfigurationId)
				.orElseThrow(() -> new UnprocessableException("pvModuleConfiguration.not.found"));

		pvModuleConfiguration.setIsActive(false);
		moduleConfigurationRepository.save(pvModuleConfiguration);
	}

	
}
