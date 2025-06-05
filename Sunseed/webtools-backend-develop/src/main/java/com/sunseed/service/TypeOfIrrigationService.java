package com.sunseed.service;

import java.util.List;

import com.sunseed.entity.Irrigation;
import com.sunseed.model.requestDTO.masterTables.TypeOfIrrigationRequestDto;

public interface TypeOfIrrigationService {

	List<Irrigation> getIrrigationDetails();

	List<Irrigation> getActiveIrrigationDetails();

	List<Irrigation> getIrrigationDetails(String search);

	Irrigation getTypeOfIrrigationById(Long typeOfIrrigationId);

	Irrigation addTypeOfIrrigation(TypeOfIrrigationRequestDto requestDto);

	Irrigation updateTypeOfIrrigation(TypeOfIrrigationRequestDto requestDto, Long typeOfIrrigationId);

	void deleteTypeOfIrrigation(Long typeOfIrrigationId);

}
