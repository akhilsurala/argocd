package com.sunseed.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.ProtectionLayer;
import com.sunseed.model.requestDTO.masterTables.ProtectionLayerRequestDto;

public interface ProtectionLayerService {

	List<ProtectionLayer> getProtectionLayers();

	List<ProtectionLayer> getProtectionLayers(String search);

	List<ProtectionLayer> getActiveProtectionLayers();

	ProtectionLayer getProtectionLayerById(Long protectionLayerId);

	ProtectionLayer addProtectionLayer(ProtectionLayerRequestDto requestDto, List<MultipartFile> opticalFiles, MultipartFile texture);

	ProtectionLayer updateProtectionLayer(ProtectionLayerRequestDto requestDto, Long protectionLayerId, List<MultipartFile> opticalFiles, MultipartFile texture);

	void deleteProtectionLayer(Long protectionLayerId);
}
