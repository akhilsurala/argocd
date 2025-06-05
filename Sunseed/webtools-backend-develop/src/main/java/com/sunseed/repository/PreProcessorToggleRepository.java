package com.sunseed.repository;

import java.util.Optional;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.PreProcessorToggle;
import com.sunseed.enums.PreProcessorStatus;

@Repository
public interface PreProcessorToggleRepository extends JpaRepository<PreProcessorToggle, Long> {

	Optional<PreProcessorToggle> findByProjectProjectIdAndPreProcessorStatus(Long projectId,
			PreProcessorStatus preProcessorStatus);

	@Query("SELECT p.toggle FROM PreProcessorToggle p WHERE p.project.projectId = :projectId AND p.preProcessorStatus = :preProcessorStatus")
	String findToggleByProjectProjectIdAndStatus(Long projectId, PreProcessorStatus preProcessorStatus);
	
	@Query("SELECT pt FROM PreProcessorToggle pt WHERE pt.project.projectId = :projectId AND pt.preProcessorStatus = 'DRAFT'")
	List<PreProcessorToggle> findByProjectIdAndStatus(Long projectId);

	@Query("SELECT COUNT(p)>0 FROM PreProcessorToggle p WHERE p.project.projectId = :projectId AND LOWER(p.runName) = LOWER(:runName)")
	boolean findByProjectProjectIdAndRunName(Long projectId, String runName);

	@Query("SELECT p.id FROM PreProcessorToggle p WHERE p.project.projectId = :projectId AND LOWER(p.runName) = LOWER(:runName)")
	Long findExistingToggleId(Long projectId, String runName);
}
