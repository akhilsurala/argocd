package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.CropParameters;
import com.sunseed.entity.EconomicParameters;
import com.sunseed.entity.Projects;
import com.sunseed.enums.PreProcessorStatus;

public interface EconomicParameterRepository extends JpaRepository<EconomicParameters,Long> {
    Optional<EconomicParameters> findByProjectAndStatus(Projects project, PreProcessorStatus status);
    
    @Query("SELECT cp FROM EconomicParameters cp WHERE cp.status = 'DRAFT' AND cp.project.id = :projectId")
    List<EconomicParameters> getEconomicParametersWithDraft(@Param("projectId") Long projectId);

}
