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

import com.sunseed.entity.Irrigation;
import com.sunseed.model.requestDTO.masterTables.TypeOfIrrigationRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.TypeOfIrrigationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class TypeOfIrrigationAdminController {

	private final ApiResponse apiResponse;
	private final TypeOfIrrigationService typeOfIrrigationService;

	@PostMapping("/typeOfIrrigation")
	public ResponseEntity<Object> addTypeOfIrrigation(@Valid @RequestBody TypeOfIrrigationRequestDto requestDto) {

		Irrigation typeOfIrrigation = typeOfIrrigationService.addTypeOfIrrigation(requestDto);

		return apiResponse.ResponseHandler(true, "typeOfIrrigation.added", HttpStatus.OK, typeOfIrrigation);
	}

	@PutMapping("/typeOfIrrigation/{typeOfIrrigationId}")
	public ResponseEntity<Object> updateTypeOfIrrigation(@Valid @RequestBody TypeOfIrrigationRequestDto requestDto,
			@PathVariable("typeOfIrrigationId") Long typeOfIrrigationId) {

		Irrigation typeOfIrrigation = typeOfIrrigationService.updateTypeOfIrrigation(requestDto,
				typeOfIrrigationId);

		return apiResponse.ResponseHandler(true, "typeOfIrrigation.updated", HttpStatus.OK, typeOfIrrigation);
	}

	@DeleteMapping("/typeOfIrrigation/{typeOfIrrigationId}")
	public ResponseEntity<Object> deleteTypeOfIrrigation(@PathVariable("typeOfIrrigationId") Long typeOfIrrigationId) {

		typeOfIrrigationService.deleteTypeOfIrrigation(typeOfIrrigationId);
		return apiResponse.ResponseHandler(true, "typeOfIrrigation.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/typeOfIrrigations")
	public ResponseEntity<Object> getIrrigationDetails(@Nullable @RequestParam(name = "search", required = false) String search) {

		List<Irrigation> typeOfIrrigations = typeOfIrrigationService.getIrrigationDetails(search);

		return apiResponse.ResponseHandler(true, "typeOfIrrigation.fetched", HttpStatus.OK, typeOfIrrigations);
	}

	@GetMapping("/typeOfIrrigation/{typeOfIrrigationId}")
	public ResponseEntity<Object> getTypeOfIrrigationById(@PathVariable("typeOfIrrigationId") Long typeOfIrrigationId) {
		Irrigation typeOfIrrigation = typeOfIrrigationService.getTypeOfIrrigationById(typeOfIrrigationId);
		return apiResponse.ResponseHandler(true, "typeOfIrrigation.fetched", HttpStatus.OK, typeOfIrrigation);
	}
}
