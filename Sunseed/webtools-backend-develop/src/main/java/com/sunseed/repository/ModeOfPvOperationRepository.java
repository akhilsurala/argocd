package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.entity.ModeOfPvOperation;

public interface ModeOfPvOperationRepository extends JpaRepository<ModeOfPvOperation, Long> {

	List<ModeOfPvOperation> findAllByOrderByModeOfOperationAsc();

	List<ModeOfPvOperation> findByIsActiveTrueAndHideFalseOrderByModeOfOperationAsc();

	Optional<ModeOfPvOperation> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT m FROM ModeOfPvOperation m WHERE LOWER(m.modeOfOperation) = LOWER(:modeOfOperation)")
	Optional<ModeOfPvOperation> findByModeOfOperationIgnoreCase(@Param("modeOfOperation") String modeOfOperation);

	@Query("SELECT m.id FROM ModeOfPvOperation m WHERE LOWER(m.modeOfOperation) = LOWER(:modeOfOperation)")
	Long findIdWithModeOfOperationIgnoreCase(@Param("modeOfOperation") String modeOfOperation);

	@Query("SELECT m FROM ModeOfPvOperation m WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(m.modeOfOperation) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND m.isActive = true ")
	List<ModeOfPvOperation> findAllBySearchOrderByNameAsc(String searchString);
}
