package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.PvParameter;
import com.sunseed.enums.PreProcessorStatus;

public interface PvParameterRepository extends JpaRepository<PvParameter, Long> {

    // @Query(value="SELECT pv FROM pv_parameter pv WHERE pv.pre_processing_status
    // ='DRAFT'",nativeQuery = true)

    @Query("SELECT pv FROM PvParameter pv WHERE pv.status = 'DRAFT' AND pv.project.id = :projectId")
    PvParameter findByProjectIdAndStatusDraft(@Param("projectId") Long projectId);
    
    Optional<PvParameter> findByProjectProjectIdAndStatus(Long projectId,PreProcessorStatus status);
    List<PvParameter> findAllByProjectProjectIdAndStatus(Long projectId, PreProcessorStatus status);


    
    @Query("SELECT pv FROM PvParameter pv WHERE pv.status = 'DRAFT' AND pv.project.id = :projectId")
	List<PvParameter> getPvParameterWithStatusDraft(@Param("projectId") Long projectId);
}
