package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.PvModuleConfiguration;

public interface PvModuleConfigurationRepository extends JpaRepository<PvModuleConfiguration, Long> {

	List<PvModuleConfiguration> findAllByOrderByModuleConfigAsc();

	Optional<PvModuleConfiguration> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT p FROM PvModuleConfiguration p WHERE LOWER(p.moduleConfig) = LOWER(:moduleConfig)")
	Optional<PvModuleConfiguration> findByModuleConfigIgnoreCase(@Param("moduleConfig") String moduleConfig);

	@Query("SELECT p.id FROM PvModuleConfiguration p WHERE LOWER(p.moduleConfig) = LOWER(:moduleConfig)")
	Long findIdWithModuleConfigIgnoreCase(@Param("moduleConfig") String moduleConfig);

	List<PvModuleConfiguration> findAllByOrderByOrderingAsc();

	List<PvModuleConfiguration> findByIsActiveTrueAndHideFalseOrderByOrderingAsc();

	boolean existsByOrdering(int ordering);

	boolean existsByOrderingAndIdNot(int ordering, Long id);

	@Query("SELECT p FROM PvModuleConfiguration p WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(p.moduleConfig) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
		      + "AND p.isActive = true ORDER BY p.ordering ASC")
	List<PvModuleConfiguration> findAllBySearchOrderByOrderingAsc(String searchString);

}
