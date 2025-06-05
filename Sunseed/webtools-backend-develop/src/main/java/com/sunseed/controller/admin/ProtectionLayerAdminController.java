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
import com.sunseed.entity.ProtectionLayer;
import com.sunseed.model.requestDTO.masterTables.ProtectionLayerRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.ProtectionLayerService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class ProtectionLayerAdminController {

	private final ApiResponse apiResponse;
	private final ProtectionLayerService protectionLayerService;

	@PostMapping("/protectionLayer")
	@Operation(summary = "Add a new ProtectionLayer", description = "Uploads Protection Layer details along with optical files.")
//	public ResponseEntity<Object> addProtectionLayer(@Valid @RequestBody ProtectionLayerRequestDto requestDto) {
	public ResponseEntity<Object> addProtectionLayer(@RequestParam("requestDto") String requestDtoJson,
			@RequestParam(value = "texture", required = false) MultipartFile texture,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles)
			throws JsonMappingException, JsonProcessingException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		ProtectionLayerRequestDto requestDto = objectMapper.readValue(requestDtoJson, ProtectionLayerRequestDto.class);
		ProtectionLayer protectionLayer = protectionLayerService.addProtectionLayer(requestDto, opticalFiles, texture);
		return apiResponse.ResponseHandler(true, "protectionLayer.added", HttpStatus.OK, protectionLayer);
	}

	@PutMapping("/protectionLayer/{protectionLayerId}")
	public ResponseEntity<Object> updateProtectionLayer(@RequestParam("requestDto") String requestDtoJson,
			@RequestParam(value = "texture", required = false) MultipartFile texture,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles,
			@PathVariable("protectionLayerId") Long protectionLayerId)
			throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
		ProtectionLayerRequestDto requestDto = objectMapper.readValue(requestDtoJson, ProtectionLayerRequestDto.class);

		ProtectionLayer protectionLayer = protectionLayerService.updateProtectionLayer(requestDto, protectionLayerId,
				opticalFiles, texture);
		return apiResponse.ResponseHandler(true, "protectionLayer.updated", HttpStatus.OK, protectionLayer);
	}

	@DeleteMapping("/protectionLayer/{protectionLayerId}")
	public ResponseEntity<Object> deleteProtectionLayer(@PathVariable("protectionLayerId") Long protectionLayerId) {

		protectionLayerService.deleteProtectionLayer(protectionLayerId);
		return apiResponse.ResponseHandler(true, "protectionLayer.deleted", HttpStatus.OK, null);
	}

	@GetMapping("/protectionLayers")
	public ResponseEntity<Object> getProtectionLayers(
			@Nullable @RequestParam(name = "search", required = false) String search) {

		List<ProtectionLayer> protectionLayers = protectionLayerService.getProtectionLayers(search);
		return apiResponse.ResponseHandler(true, "protectionLayer.fetched", HttpStatus.OK, protectionLayers);
	}

	@GetMapping("/protectionLayer/{protectionLayerId}")
	public ResponseEntity<Object> getProtectionLayerById(@PathVariable("protectionLayerId") Long protectionLayerId) {
		ProtectionLayer protectionLayer = protectionLayerService.getProtectionLayerById(protectionLayerId);
		return apiResponse.ResponseHandler(true, "protectionLayer.fetched", HttpStatus.OK, protectionLayer);
	}
}
