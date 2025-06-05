package com.sunseed.simtool.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.sunseed.simtool.model.request.SimulationStatusDto;
import com.sunseed.simtool.model.response.SimulationTaskDto;
import com.sunseed.simtool.model.response.SimulationTaskStatusDto;

public interface SimulationService {

	Map<String,List<SimulationTaskStatusDto>> updateStatus(SimulationStatusDto statusDto);

	Map<String, List<SimulationTaskDto>> getSimulationResult(Long id);
	
	byte[] getFileFromSunseedGiz(String filename) throws IOException;

	String getFileContentType(String filename) throws IOException;
	
	String uploadTextureFile(MultipartFile file) throws IOException;

	String deleteTextureFile(String fileName) throws IOException;

}
