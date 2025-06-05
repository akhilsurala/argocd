package com.sunseed.serviceImpl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.repository.PreProcessorToggleRepository;
import com.sunseed.service.PreProcessorToggleService;

@Service
public class PreProcessorToggleServiceImpl implements PreProcessorToggleService {
	
	 @Autowired
	  private PreProcessorToggleRepository repository;

	
	 @Override
	  public PreProcessorToggle getPreProcessorToggles(Long projectId) {
	    // find PreProcessorToggles by projectId and draft status
	    List<PreProcessorToggle> preProcessorToggles = repository.findByProjectIdAndStatus(projectId);

	    return preProcessorToggles.isEmpty() ? null : preProcessorToggles.get(0);
	  }

}
