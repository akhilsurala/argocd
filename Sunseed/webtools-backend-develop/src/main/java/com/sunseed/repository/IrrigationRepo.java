package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.Irrigation;

public interface IrrigationRepo extends JpaRepository<Irrigation, Long> {

	List<Irrigation> findAllByOrderByIrrigationTypeAsc();

	List<Irrigation> findByIsActiveTrueAndHideFalseOrderByIrrigationTypeAsc();

	Optional<Irrigation> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT t FROM Irrigation t WHERE LOWER(t.irrigationType) = LOWER(:irrigationType)")
	Optional<Irrigation> findByIrrigationTypeIgnoreCase(@Param("irrigationType") String irrigationType);

	@Query("SELECT t.id FROM Irrigation t WHERE LOWER(t.irrigationType) = LOWER(:irrigationType)")
	Long findIdWithIrrigationTypeIgnoreCase(@Param("irrigationType") String irrigationType);

	@Query("SELECT i FROM Irrigation i WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(i.irrigationType) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND i.isActive = true")
	List<Irrigation> findAllBySearchNameOrderByNameAsc(String searchString);
}
