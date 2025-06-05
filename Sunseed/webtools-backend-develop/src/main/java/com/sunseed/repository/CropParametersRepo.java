package com.sunseed.repository;

import java.util.*;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.CropParameters;
import com.sunseed.entity.Projects;
import com.sunseed.enums.PreProcessorStatus;

@Repository
public interface CropParametersRepo extends JpaRepository<CropParameters, Long>{

	Optional<CropParameters> findByProjectAndStatus(Projects project, PreProcessorStatus draft);
	
	Optional<CropParameters> findByProjectProjectIdAndStatus(Long projectId,PreProcessorStatus status);
	
	@Query("SELECT cp FROM CropParameters cp WHERE cp.status = 'DRAFT' AND cp.project.id = :projectId")
    List<CropParameters> getCropParametersWithDraft(@Param("projectId") Long projectId);

//	Optional<CropParameters> findByRun(Long runId);
}
