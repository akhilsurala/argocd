package com.sunseed.controller.cropParameters;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sunseed.entity.Crop;
import com.sunseed.model.responseDTO.CropResponse;
import com.sunseed.response.ApiResponse;
import com.sunseed.service.CropService;

@RestController
@RequestMapping("/v1/crops")
public class CropController {
	
	@Autowired
	private ApiResponse apiResponse;
	
	@Autowired
	private CropService cropService; 

	@GetMapping("")
	public ResponseEntity<Object> getCrops() {

		List<Crop> activeCrops = cropService.getCrops();
	
		List<CropResponse> crops = activeCrops.stream()
				.map(crop -> CropResponse.builder()
				.id(crop.getId())
				.name(crop.getName())
				.duration(crop.getDuration())
				.meta(crop)
				.build())
				.collect(Collectors.toList());
		

		return apiResponse.ResponseHandler(true, "crops.fetched", HttpStatus.OK, crops);
	}
}
