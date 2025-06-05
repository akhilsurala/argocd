package com.sunseed.simtool.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.simtool.entity.E2EMachineNode;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;

public interface E2EMachineNodeRepository extends JpaRepository<E2EMachineNode, Long> {

	@Query("SELECT a FROM E2EMachineNode a WHERE LOWER(a.status) = LOWER(:status)")
	List<E2EMachineNode> findAllByStatusIgnoreCase(@Param("status") String status);

	@Query(value = """
			    SELECT * FROM simtool.e2e_machine_nodes
			    WHERE LOWER(status) = LOWER(:status)
			      AND load < capacity
			    ORDER BY
			      CASE WHEN (load + :cpuCount) <= capacity THEN 0 ELSE 1 END,
			      load DESC,
			      node_id ASC
			    LIMIT 1
			""", nativeQuery = true)
	Optional<E2EMachineNode> findBestFitNode(@Param("cpuCount") int cpuCount, @Param("status") String status);

	@Query(value = """
			    SELECT * FROM (
			        SELECT *,
			            GREATEST(
			                100 *(CAST(:taskCpu AS float) / NULLIF(vcpus - :freeCpu, 0)),
			                100 *(CAST(:taskMemory AS float) / NULLIF(
			                    CAST(REGEXP_REPLACE(memory, '\\s*GB', '', 'i') AS float) - :freeMemory, 0)
			            )) AS load_required
			        FROM simtool.e2e_machine_nodes
			        WHERE LOWER(status) = LOWER(:status)
			    ) AS nodes_with_load
			    WHERE (current_load + load_required) <= 100
			    ORDER BY current_load DESC, node_id ASC
			    LIMIT 1
			""", nativeQuery = true)
	Optional<E2EMachineNode> findBestFitNode(@Param("taskCpu") double taskCpu, @Param("taskMemory") double taskMemory,
			@Param("freeCpu") double freeCpu, @Param("freeMemory") double freeMemory, @Param("status") String status);

	@Modifying
	@Query("""
			    UPDATE E2EMachineNode e
			    SET e.currentLoad = e.currentLoad - :load
			    WHERE e.nodeId = :nodeId AND e.currentLoad >= :load
			""")
	int decrementLoadByNodeIdIfPossible(@Param("nodeId") Long nodeId, @Param("load") BigDecimal load);

	@Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM E2EMachineNode e WHERE LOWER(e.status) = LOWER(:status)")
	boolean existsByStatus(@Param("status") String string);

	// wherever use this method always pass the lowercase values
	@Query("SELECT COUNT(e) FROM E2EMachineNode e WHERE LOWER(e.status) IN :statuses")
	int countByStatusIn(@Param("statuses") List<String> statuses);

	// wherever use this method always pass the lowercase values
	@Query("SELECT e FROM E2EMachineNode e WHERE LOWER(e.status) IN :statuses")
	List<E2EMachineNode> findAllByStatusIn(@Param("statuses") List<String> statuses);

	Optional<E2EMachineNode> findByNodeId(Long nodeId);

	@Query("""
			    SELECT n FROM E2EMachineNode n
			    WHERE LOWER(n.status) = 'running' AND n.currentLoad = 0.0
			""")
	List<E2EMachineNode> findRunningNodesWithZeroLoad();

	@Query("SELECT n FROM E2EMachineNode n WHERE LOWER(n.status) = 'running'")
	List<E2EMachineNode> findAllRunningNodes();

	@Modifying
	@Transactional
	@Query("UPDATE E2EMachineNode n SET n.currentLoad = :load WHERE LOWER(n.status) = 'running'")
	void resetAllRunningNodeLoadToZero(@Param("load") BigDecimal load);

	@Query(value = """
			SELECT COUNT(*) > 0
			FROM simtool.e2e_machine_nodes
			WHERE LOWER(status) = 'running'
			  AND vcpus >= :requiredCpu
			  AND CAST(regexp_replace(memory, '[^0-9\\.]', '', 'g') AS DOUBLE PRECISION) >= :requiredRam
			""", nativeQuery = true)
	boolean existsNodeWithSufficientResources(@Param("requiredCpu") int requiredCpu,
			@Param("requiredRam") double requiredRam);
	
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT e FROM E2EMachineNode e WHERE e.nodeId = :nodeId")
	Optional<E2EMachineNode> findByNodeIdForUpdate(@Param("nodeId") Long nodeId);
}