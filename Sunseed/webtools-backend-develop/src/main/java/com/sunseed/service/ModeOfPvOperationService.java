package com.sunseed.service;

import java.util.List;

import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.model.requestDTO.masterTables.ModeOfPvOperationRequestDto;

public interface ModeOfPvOperationService {
	
	List<ModeOfPvOperation> getModeOfOperations(String search);
	List<ModeOfPvOperation> getActiveModeOfOperations();
	ModeOfPvOperation getModeOfOperationById(Long modeOfOperationId);
	ModeOfPvOperation addModeOfOperation(ModeOfPvOperationRequestDto requestDto);
	ModeOfPvOperation updateModeOfOperation(ModeOfPvOperationRequestDto requestDto, Long modeOfOperationId);
	void deleteModeOfOperation(Long modeOfOperationId);
}
