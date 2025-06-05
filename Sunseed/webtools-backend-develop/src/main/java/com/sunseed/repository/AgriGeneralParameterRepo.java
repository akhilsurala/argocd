package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.Projects;
import com.sunseed.enums.PreProcessorStatus;

public interface AgriGeneralParameterRepo extends JpaRepository<AgriGeneralParameter, Long>{
//	@Query("SELECT a FROM AgriGeneralParameter a WHERE a.status = :status") --> this is the query to be used in the service layer
	//dynamic query
	
//	AgriGeneralParameter findByStatus(Long projectId, PreStatus status);

	Optional<AgriGeneralParameter> findByProject(Projects project);

	Optional<AgriGeneralParameter> findByProjectAndStatus(Projects project, PreProcessorStatus status);
	
	Optional<AgriGeneralParameter> findByProjectProjectIdAndStatus(Long projectId,PreProcessorStatus status);
	
	@Query("SELECT cp FROM AgriGeneralParameter cp WHERE cp.status = 'DRAFT' AND cp.project.id = :projectId")
    List<AgriGeneralParameter> getAgriGeneralParametersWithDraft(@Param("projectId") Long projectId);
	
}
