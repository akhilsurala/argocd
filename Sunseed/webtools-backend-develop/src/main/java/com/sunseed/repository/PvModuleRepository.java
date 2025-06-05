package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.PvModule;

public interface PvModuleRepository extends JpaRepository<PvModule, Long> {

	List<PvModule> findAllByOrderByModuleTypeAsc();

	List<PvModule> findByIsActiveTrueAndHideFalseOrderByModuleTypeAsc();

	Optional<PvModule> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT p FROM PvModule p WHERE LOWER(p.moduleType) = LOWER(:moduleType)")
	Optional<PvModule> findByModuleTypeIgnoreCase(@Param("moduleType") String moduleType);

	@Query("SELECT p.id FROM PvModule p WHERE LOWER(p.moduleType) = LOWER(:moduleType)")
	Long findIdWithModuleTypeIgnoreCase(@Param("moduleType") String moduleType);

	@Query("SELECT p FROM PvModule p WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(p.moduleType) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND p.isActive = true ")
	List<PvModule> findAllBySearchOrderByNameAsc(String searchString);
	
	@Query("SELECT p FROM PvModule p WHERE LOWER(p.moduleType) = LOWER(:moduleType)")
	List<PvModule> findAllByModuleTypeIgnoreCase(@Param("moduleType") String moduleType);
}
