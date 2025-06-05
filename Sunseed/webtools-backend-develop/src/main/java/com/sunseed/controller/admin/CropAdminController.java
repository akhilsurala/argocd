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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.Crop;
import com.sunseed.model.requestDTO.masterTables.CropRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.CropService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class CropAdminController {

	private final ApiResponse apiResponse;
	private final CropService cropService;

	@PostMapping("/crop")
	@Operation(summary = "Add a new Crop", description = "Uploads Crop details along with optical files.")
//  public ResponseEntity<Object> addCrop(@Valid @RequestBody CropRequestDto requestDto) {
	public ResponseEntity<Object> addCrop(@RequestParam("requestDto") String requestDtoJson,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles)
			throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		CropRequestDto requestDto = objectMapper.readValue(requestDtoJson, CropRequestDto.class);
		Crop crop = cropService.addCrop(requestDto, opticalFiles);

		return apiResponse.ResponseHandler(true, "crop.added", HttpStatus.OK, crop);
	}

	@PutMapping("/crop/{cropId}")
//  public ResponseEntity<Object> updateCrop(@Valid @RequestBody CropRequestDto requestDto,
//      @PathVariable("cropId") Long cropId) {
	public ResponseEntity<Object> updateCrop(@RequestParam("requestDto") String requestDtoJson,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles,
			@PathVariable("cropId") Long cropId) throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		CropRequestDto requestDto = objectMapper.readValue(requestDtoJson, CropRequestDto.class);


		Crop crop = cropService.updateCrop(requestDto, cropId, opticalFiles);

		return apiResponse.ResponseHandler(true, "crop.updated", HttpStatus.OK, crop);
	}

	@DeleteMapping("/crop/{cropId}")
	public ResponseEntity<Object> deleteCrop(@PathVariable("cropId") Long cropId) {

		cropService.deleteCrop(cropId);
		return apiResponse.ResponseHandler(true, "crop.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/crops")
	public ResponseEntity<Object> getCrops(@Nullable @RequestParam(name = "search", required = false) String search) {

		List<Crop> crops = cropService.getCrops(search);

		return apiResponse.ResponseHandler(true, "crop.fetched", HttpStatus.OK, crops);
	}

	@GetMapping("/crop/{cropId}")
	public ResponseEntity<Object> getCropById(@PathVariable("cropId") Long cropId) {
		Crop crop = cropService.getCropById(cropId);
		return apiResponse.ResponseHandler(true, "crop.fetched", HttpStatus.OK, crop);
	}
}
