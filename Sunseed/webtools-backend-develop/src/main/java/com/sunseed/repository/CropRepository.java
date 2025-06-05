package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.Crop;

@Repository
public interface CropRepository extends JpaRepository<Crop, Long> {

	List<Crop> findByIsActiveTrueAndHideFalseOrderByNameAsc();

	List<Crop> findAllByOrderByNameAsc();
	
    List<Crop> findByNameIn(List<String> names);

	Optional<Crop> findByIdAndIsActiveTrue(Long id);

	@Query("SELECT c FROM Crop c WHERE LOWER(c.name) = LOWER(:name)")
	Optional<Crop> findByNameIgnoreCase(@Param("name") String name);

	@Query("SELECT c.id FROM Crop c WHERE LOWER(c.name) = LOWER(:name)")
	Long findIdWithNameIgnoreCase(@Param("name") String name);

	@Query("SELECT c FROM Crop c WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(c.name) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND c.isActive = true")
	List<Crop> findAllBySearchNameOrderByNameAsc(String searchString);

}
