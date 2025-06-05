package com.sunseed.repository;

import com.sunseed.entity.EconomicMultiCrop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EconomicMultiCropRepository extends JpaRepository<EconomicMultiCrop, Long> {
    @Query("SELECT e FROM EconomicMultiCrop e WHERE e.crop.id = :cropId AND e.economicParameters.economicId = :economicId")
    EconomicMultiCrop getEconomicMultiCropByCropAndEconomicParameterId(Long cropId, Long economicId);
}
