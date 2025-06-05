package com.sunseed.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.SoilType;
import com.sunseed.model.requestDTO.masterTables.SoilRequestDto;

public interface SoilService {

	List<SoilType> getSoilDetails();
	List<SoilType> getActiveSoilDetails();
	List<SoilType> getSoilDetails(String search);
	SoilType getSoilById(Long soilId);
	SoilType addSoil(SoilRequestDto requestDto,MultipartFile image, List<MultipartFile> opticalFiles);
	SoilType updateSoil(SoilRequestDto requestDto, Long soilId,MultipartFile image, List<MultipartFile> opticalFiles);
	void deleteSoil(Long soilId);
	
}
