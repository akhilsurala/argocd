package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.ProtectionLayer;

@Repository
public interface ProtectionLayerRepo extends JpaRepository<ProtectionLayer, Long> {

	List<ProtectionLayer> findAllByProtectionLayerIdIn(List<Long> protectionIdList);

	List<ProtectionLayer> findAllByOrderByProtectionLayerNameAsc();

	List<ProtectionLayer> findByIsActiveTrueAndHideFalseOrderByProtectionLayerNameAsc();

	Optional<ProtectionLayer> findByProtectionLayerIdAndIsActiveTrue(Long id);

	@Query("SELECT p FROM ProtectionLayer p WHERE LOWER(p.protectionLayerName) = LOWER(:protectionLayerName)")
	Optional<ProtectionLayer> findByProtectionLayerNameIgnoreCase(
			@Param("protectionLayerName") String protectionLayerName);

	@Query("SELECT p.id FROM ProtectionLayer p WHERE LOWER(p.protectionLayerName) = LOWER(:protectionLayerName)")
	Long findIdWithProtectionLayerNameIgnoreCase(@Param("protectionLayerName") String protectionLayerName);

	@Query("SELECT p FROM ProtectionLayer p WHERE (:searchString IS NOT NULL AND :searchString <> '' AND LOWER(p.protectionLayerName) LIKE LOWER(CONCAT('%', :searchString, '%'))) "
			+ "AND p.isActive = true")
	List<ProtectionLayer> findAllBySearchOrderByNameAsc(String searchString);

}
