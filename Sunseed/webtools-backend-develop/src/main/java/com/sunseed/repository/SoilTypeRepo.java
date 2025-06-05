package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.SoilType;

public interface SoilTypeRepo extends JpaRepository<SoilType, Long> {

	List<SoilType> findAllByOrderBySoilNameAsc();

	List<SoilType> findByIsActiveTrueAndHideFalseOrderBySoilNameAsc();

	Optional<SoilType> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT s FROM SoilType s WHERE LOWER(s.soilName) = LOWER(:soilName)")
	Optional<SoilType> findBySoilNameIgnoreCase(@Param("soilName") String soilName);

	@Query("SELECT s.id FROM SoilType s WHERE LOWER(s.soilName) = LOWER(:soilName)")
	Long findIdWithSoilNameIgnoreCase(@Param("soilName") String soilName);

	@Query("SELECT s FROM SoilType s WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(s.soilName) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND s.isActive = true")
	List<SoilType> findAllBySearchOrderByNameAsc(String searchString);
}
