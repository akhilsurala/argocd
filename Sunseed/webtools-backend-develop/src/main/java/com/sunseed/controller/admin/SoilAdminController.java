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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.entity.SoilType;
import com.sunseed.model.requestDTO.masterTables.SoilRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.SoilService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class SoilAdminController {

	private final ApiResponse apiResponse;
	private final SoilService soilService;

	@PostMapping("/soil")
//	public ResponseEntity<Object> addSoil(@RequestParam("name") String name, @RequestParam("hide") Boolean hide,
//	@RequestParam(value = "soilPic", required = false) MultipartFile image) {
	public ResponseEntity<Object> addSoil(@RequestParam("requestDto") String requestDtoJson,
			@RequestParam(value = "soilPic", required = false) MultipartFile image,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles)
			throws JsonMappingException, JsonProcessingException {

//SoilRequestDto requestDto = SoilRequestDto.builder().name(name).hide(hide).build();
		ObjectMapper objectMapper = new ObjectMapper();
		SoilRequestDto requestDto = objectMapper.readValue(requestDtoJson, SoilRequestDto.class);

		SoilType soil = soilService.addSoil(requestDto, image, opticalFiles);

		return apiResponse.ResponseHandler(true, "soil.added", HttpStatus.OK, soil);
	}

	@PutMapping("/soil/{soilId}")
	public ResponseEntity<Object> updateSoil(@RequestParam("requestDto") String requestDtoJson,
			@RequestParam(value = "soilPic", required = false) MultipartFile image,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles,
			@PathVariable("soilId") Long soilId) throws JsonMappingException, JsonProcessingException {

//		SoilRequestDto requestDto = SoilRequestDto.builder().name(name).hide(hide).build();
		ObjectMapper objectMapper = new ObjectMapper();
		SoilRequestDto requestDto = objectMapper.readValue(requestDtoJson, SoilRequestDto.class);

		SoilType soil = soilService.updateSoil(requestDto, soilId, image, opticalFiles);

		return apiResponse.ResponseHandler(true, "soil.updated", HttpStatus.OK, soil);
	}

	@DeleteMapping("/soil/{soilId}")
	public ResponseEntity<Object> deleteSoil(@PathVariable("soilId") Long soilId) {

		soilService.deleteSoil(soilId);
		return apiResponse.ResponseHandler(true, "soil.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/soils")
	public ResponseEntity<Object> getSoilDetails(@Nullable @RequestParam(name = "search", required = false) String search) {

		List<SoilType> soils = soilService.getSoilDetails(search);

		return apiResponse.ResponseHandler(true, "soil.fetched", HttpStatus.OK, soils);
	}

	@GetMapping("/soil/{soilId}")
	public ResponseEntity<Object> getSoilById(@PathVariable("soilId") Long soilId) {
		SoilType soil = soilService.getSoilById(soilId);
		return apiResponse.ResponseHandler(true, "soil.fetched", HttpStatus.OK, soil);
	}
}
