package com.sunseed.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunseed.entity.Irrigation;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.masterTables.TypeOfIrrigationRequestDto;
import com.sunseed.repository.IrrigationRepo;
import com.sunseed.service.TypeOfIrrigationService;

@Service
public class TypeOfIrrigationServiceImpl implements TypeOfIrrigationService {

	@Autowired
	private IrrigationRepo irrigationRepository;

	@Override
	public List<Irrigation> getIrrigationDetails() {
		List<Irrigation> irrigationTypes = irrigationRepository.findAllByOrderByIrrigationTypeAsc();
		return irrigationTypes;
	}

	@Override
	public List<Irrigation> getIrrigationDetails(String search) {
		
		if (search == null || search.trim().isEmpty()) {
			return irrigationRepository.findAllByOrderByIrrigationTypeAsc();
		} else {
			return irrigationRepository.findAllBySearchNameOrderByNameAsc(search);
		}
	}

	@Override
	public List<Irrigation> getActiveIrrigationDetails() {
		List<Irrigation> irrigationTypes = irrigationRepository
				.findByIsActiveTrueAndHideFalseOrderByIrrigationTypeAsc();
		return irrigationTypes;
	}

	@Override
	public Irrigation getTypeOfIrrigationById(Long typeOfIrrigationId) {
		if (typeOfIrrigationId == null || typeOfIrrigationId <= 0)
			throw new UnprocessableException("typeOfIrrigation.not.found");
		Optional<Irrigation> typeOfIrrigation = irrigationRepository.findByIdAndIsActiveTrue(typeOfIrrigationId);
		if (typeOfIrrigation.isEmpty())
			throw new UnprocessableException("typeOfIrrigation.not.found");
		return typeOfIrrigation.get();
	}

	@Override
	public Irrigation addTypeOfIrrigation(TypeOfIrrigationRequestDto requestDto) {
		Optional<Irrigation> optionalTypeOfIrrigation = irrigationRepository
				.findByIrrigationTypeIgnoreCase(requestDto.getName());
		if (optionalTypeOfIrrigation.isPresent())
			throw new ConflictException("typeOfIrrigation.exists");
		Irrigation newTypeOfIrrigation = Irrigation.builder().irrigationType(requestDto.getName()).build();

		Irrigation savedTypeOfIrrigation = irrigationRepository.save(newTypeOfIrrigation);
		return savedTypeOfIrrigation;
	}

	@Override
	public Irrigation updateTypeOfIrrigation(TypeOfIrrigationRequestDto requestDto, Long typeOfIrrigationId) {

		if (typeOfIrrigationId == null || typeOfIrrigationId <= 0)
			throw new UnprocessableException("typeOfIrrigation.not.found");
		Optional<Irrigation> optionalTypeOfIrrigation = irrigationRepository.findById(typeOfIrrigationId);
		if (optionalTypeOfIrrigation.isEmpty())
			throw new UnprocessableException("typeOfIrrigation.not.found");
		Long existingTypeOfIrrigationId = irrigationRepository.findIdWithIrrigationTypeIgnoreCase(requestDto.getName());
		if (existingTypeOfIrrigationId != null && existingTypeOfIrrigationId != typeOfIrrigationId)
			throw new UnprocessableException("typeOfIrrigation.exists");

		Irrigation existingTypeOfIrrigation = optionalTypeOfIrrigation.get();
		existingTypeOfIrrigation.setIrrigationType(requestDto.getName());
		existingTypeOfIrrigation.setHide(requestDto.getHide());

		Irrigation updatedTypeOfIrrigation = irrigationRepository.save(existingTypeOfIrrigation);
		return updatedTypeOfIrrigation;
	}

	@Override
	public void deleteTypeOfIrrigation(Long typeOfIrrigationId) {

		if (typeOfIrrigationId == null || typeOfIrrigationId <= 0)
			throw new UnprocessableException("typeOfIrrigation.not.found");

		Irrigation typeOfIrrigation = irrigationRepository.findById(typeOfIrrigationId)
				.orElseThrow(() -> new UnprocessableException("typeOfIrrigation.not.found"));

		typeOfIrrigation.setIsActive(false);
		irrigationRepository.save(typeOfIrrigation);
	}

}
