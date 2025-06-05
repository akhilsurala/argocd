package com.sunseed.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.Crop;
import com.sunseed.model.requestDTO.masterTables.CropRequestDto;

public interface CropService {

	List<Crop> getCrops();

	List<Crop> getCrops(String search);

	List<Crop> getActiveCrops();

	Crop getCropById(Long cropId);

	Crop addCrop(CropRequestDto requestDto, List<MultipartFile> opticalFiles);

	Crop updateCrop(CropRequestDto requestDto, Long cropId, List<MultipartFile> opticalFiles);

	void deleteCrop(Long cropId);

}
