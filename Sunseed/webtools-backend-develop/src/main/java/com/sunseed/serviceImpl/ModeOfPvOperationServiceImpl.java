package com.sunseed.serviceImpl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.masterTables.ModeOfPvOperationRequestDto;
import com.sunseed.repository.ModeOfPvOperationRepository;
import com.sunseed.service.ModeOfPvOperationService;

@Service
public class ModeOfPvOperationServiceImpl implements ModeOfPvOperationService {

	@Autowired
	private ModeOfPvOperationRepository modeOfPvOperationRepository;

	@Override
	public List<ModeOfPvOperation> getModeOfOperations(String search) {

		if (search == null || search.trim().isEmpty()) {
			return modeOfPvOperationRepository.findAllByOrderByModeOfOperationAsc();
		} else {
			return modeOfPvOperationRepository.findAllBySearchOrderByNameAsc(search.toString());
		}

	}

	@Override
	public List<ModeOfPvOperation> getActiveModeOfOperations() {

		List<ModeOfPvOperation> modes = modeOfPvOperationRepository
				.findByIsActiveTrueAndHideFalseOrderByModeOfOperationAsc();
		return modes;
	}

	@Override
	public ModeOfPvOperation getModeOfOperationById(Long modeOfOperationId) {
		if (modeOfOperationId == null || modeOfOperationId <= 0)
			throw new UnprocessableException("modeOfOperation.not.found");
		Optional<ModeOfPvOperation> modeOfOperation = modeOfPvOperationRepository
				.findByIdAndIsActiveTrue(modeOfOperationId);
		if (modeOfOperation.isEmpty())
			throw new UnprocessableException("modeOfOperation.not.found");
		return modeOfOperation.get();
	}

	@Override
	public ModeOfPvOperation addModeOfOperation(ModeOfPvOperationRequestDto requestDto) {
		Optional<ModeOfPvOperation> optionalModeOfOperation = modeOfPvOperationRepository
				.findByModeOfOperationIgnoreCase(requestDto.getName());
		if (optionalModeOfOperation.isPresent())
			throw new ConflictException("modeOfOperation.exists");
		ModeOfPvOperation newModeOfOperation = ModeOfPvOperation.builder().modeOfOperation(requestDto.getName())
				.build();

		ModeOfPvOperation savedModeOfOperation = modeOfPvOperationRepository.save(newModeOfOperation);
		return savedModeOfOperation;
	}

	@Override
	public ModeOfPvOperation updateModeOfOperation(ModeOfPvOperationRequestDto requestDto, Long modeOfOperationId) {

		if (modeOfOperationId == null || modeOfOperationId <= 0)
			throw new UnprocessableException("modeOfOperation.not.found");
		Optional<ModeOfPvOperation> optionalModeOfOperation = modeOfPvOperationRepository.findById(modeOfOperationId);
		if (optionalModeOfOperation.isEmpty())
			throw new UnprocessableException("modeOfOperation.not.found");
		Long existingModeOfOperationId = modeOfPvOperationRepository
				.findIdWithModeOfOperationIgnoreCase(requestDto.getName());
		if (existingModeOfOperationId != null && existingModeOfOperationId != modeOfOperationId)
			throw new UnprocessableException("modeOfOperation.exists");

		ModeOfPvOperation existingModeOfOperation = optionalModeOfOperation.get();
		existingModeOfOperation.setModeOfOperation(requestDto.getName());
		existingModeOfOperation.setHide(requestDto.getHide());

		ModeOfPvOperation updatedModeOfOperation = modeOfPvOperationRepository.save(existingModeOfOperation);
		return updatedModeOfOperation;
	}

	@Override
	public void deleteModeOfOperation(Long modeOfOperationId) {

		if (modeOfOperationId == null || modeOfOperationId <= 0)
			throw new UnprocessableException("modeOfOperation.not.found");

		// performing soft delete on pvModule
		ModeOfPvOperation modeOfPvOperation = modeOfPvOperationRepository.findById(modeOfOperationId)
				.orElseThrow(() -> new UnprocessableException("modeOfOperation.not.found"));

		modeOfPvOperation.setIsActive(false);
		modeOfPvOperationRepository.save(modeOfPvOperation);
	}

}
