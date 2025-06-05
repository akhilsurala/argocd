package com.sunseed.controller.admin;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.entity.ModeOfPvOperation;
import com.sunseed.model.requestDTO.masterTables.ModeOfPvOperationRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.ModeOfPvOperationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class ModeOfPvOperationAdminController {

	private final ApiResponse apiResponse;
	private final ModeOfPvOperationService modeOfPvOperationService;

	@PostMapping("/modeOfPvOperation")
	public ResponseEntity<Object> addModeOfPvOperation(@Valid @RequestBody ModeOfPvOperationRequestDto requestDto) {

		ModeOfPvOperation modeOfPvOperation = modeOfPvOperationService.addModeOfOperation(requestDto);

		return apiResponse.ResponseHandler(true, "modeOfPvOperation.added", HttpStatus.OK, modeOfPvOperation);
	}

	@PutMapping("/modeOfPvOperation/{modeOfOperationId}")
	public ResponseEntity<Object> updateModeOfPvOperation(@Valid @RequestBody ModeOfPvOperationRequestDto requestDto,
			@PathVariable("modeOfOperationId") Long modeOfOperationId) {

		ModeOfPvOperation modeOfPvOperation = modeOfPvOperationService.updateModeOfOperation(requestDto,
				modeOfOperationId);

		return apiResponse.ResponseHandler(true, "modeOfPvOperation.updated", HttpStatus.OK, modeOfPvOperation);
	}

	@DeleteMapping("/modeOfPvOperation/{modeOfOperationId}")
	public ResponseEntity<Object> deleteModeOfPvOperation(@PathVariable("modeOfOperationId") Long modeOfOperationId) {

		modeOfPvOperationService.deleteModeOfOperation(modeOfOperationId);
		return apiResponse.ResponseHandler(true, "modeOfPvOperation.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/modeOfPvOperations")
	public ResponseEntity<Object> getModeOfPvOperations(
			@Nullable @RequestParam(name = "search", required = false) String search) {

		List<ModeOfPvOperation> modeOfPvOperations = modeOfPvOperationService.getModeOfOperations(search.toString());

		return apiResponse.ResponseHandler(true, "modeOfPvOperation.fetched", HttpStatus.OK, modeOfPvOperations);
	}

	@GetMapping("/modeOfPvOperation/{modeOfOperationId}")
	public ResponseEntity<Object> getModeOfPvOperationById(@PathVariable("modeOfOperationId") Long modeOfOperationId) {
		ModeOfPvOperation modeOfPvOperation = modeOfPvOperationService.getModeOfOperationById(modeOfOperationId);
		return apiResponse.ResponseHandler(true, "modeOfPvOperation.fetched", HttpStatus.OK, modeOfPvOperation);
	}
}
