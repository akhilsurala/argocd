package com.sunseed.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.PvModule;
import com.sunseed.model.requestDTO.masterTables.PvModuleRequestDto;

public interface PvModuleService {

	List<PvModule> getPvModules();
	List<PvModule> getActivePvModules();
	List<PvModule> getPvModules(String search);
	PvModule getPvModuleById(Long pvModuleId);
	PvModule addPvModule(PvModuleRequestDto requestDto, List<MultipartFile> opticalFiles);
	PvModule updatePvModule(PvModuleRequestDto requestDto, Long pvModuleId, List<MultipartFile> opticalFiles);
	void deletePvModule(Long pvModuleId);
	

}