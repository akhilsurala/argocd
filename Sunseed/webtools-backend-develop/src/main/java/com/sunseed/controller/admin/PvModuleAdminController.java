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
import com.sunseed.entity.PvModule;
import com.sunseed.model.requestDTO.masterTables.PvModuleRequestDto;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.PvModuleService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/admin")
public class PvModuleAdminController {

	private final ApiResponse apiResponse;
	private final PvModuleService pvModuleService;
	
	@PostMapping("/pvModule")
//	public ResponseEntity<Object> addPvModule(@Valid @RequestBody PvModuleRequestDto requestDto) {
	@Operation(summary = "Add a new PV Module", description = "Uploads PV module details along with optical files.")
	public ResponseEntity<Object> addPvModule(@RequestParam("requestDto") String requestDtoJson,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles) throws JsonMappingException, JsonProcessingException
	{
		
		ObjectMapper objectMapper = new ObjectMapper();
        PvModuleRequestDto requestDto = objectMapper.readValue(requestDtoJson, PvModuleRequestDto.class);

		PvModule pvModule = pvModuleService.addPvModule(requestDto, opticalFiles);

		return apiResponse.ResponseHandler(true, "pvModule.added", HttpStatus.OK, pvModule);
	}
	
	@PutMapping("/pvModule/{pvModuleId}")
	public ResponseEntity<Object> updatePvModule(@RequestParam("requestDto") String requestDtoJson,
			@RequestPart(value = "opticalFiles", required = false) List<MultipartFile> opticalFiles
			, @PathVariable("pvModuleId") Long pvModuleId) throws JsonMappingException, JsonProcessingException {

		ObjectMapper objectMapper = new ObjectMapper();
        PvModuleRequestDto requestDto = objectMapper.readValue(requestDtoJson, PvModuleRequestDto.class);
		PvModule pvModule = pvModuleService.updatePvModule(requestDto,pvModuleId, opticalFiles);

		return apiResponse.ResponseHandler(true, "pvModule.updated", HttpStatus.OK, pvModule);
	}
	
	@DeleteMapping("/pvModule/{pvModuleId}")
	public ResponseEntity<Object> deletePvModule(@PathVariable("pvModuleId") Long pvModuleId) {

		pvModuleService.deletePvModule(pvModuleId);
		return apiResponse.ResponseHandler(true, "pvModule.deleted", HttpStatus.OK, null);
	}
	
	@GetMapping("/pvModules")
	public ResponseEntity<Object> getPvModules(@Nullable @RequestParam(name = "search", required = false) String search) {

		List<PvModule> pvModules = pvModuleService.getPvModules(search);

		return apiResponse.ResponseHandler(true, "pvModule.fetched", HttpStatus.OK, pvModules);
	}
	
	@GetMapping("/pvModule/{pvModuleId}")
	public ResponseEntity<Object> getPvModuleById(@PathVariable("pvModuleId") Long pvModuleId){
		PvModule pvModule  = pvModuleService.getPvModuleById(pvModuleId);
		return apiResponse.ResponseHandler(true, "pvModule.fetched", HttpStatus.OK, pvModule);
	}
	
}
