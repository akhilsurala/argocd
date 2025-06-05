package com.sunseed.simtool.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sunseed.simtool.constant.Status;
import com.sunseed.simtool.entity.Simulation;
import com.sunseed.simtool.entity.SimulationTask;
import com.sunseed.simtool.model.response.SimulationTaskStatusDto;

public interface SimulationTaskRepository extends JpaRepository<SimulationTask, Long> {

	@Query("SELECT new com.sunseed.simtool.model.response.SimulationTaskStatusDto(st.pvStatus, COUNT(st.pvStatus)) FROM "
			+ "SimulationTask AS st WHERE st.simulation = :id GROUP BY st.pvStatus")
	List<SimulationTaskStatusDto> countTaskByPvStatus(Simulation id);
	
	@Query("SELECT new com.sunseed.simtool.model.response.SimulationTaskStatusDto(st.agriStatus, COUNT(st.agriStatus)) FROM "
			+ "SimulationTask AS st WHERE st.simulation = :id GROUP BY st.agriStatus")
	List<SimulationTaskStatusDto> countTaskByAgriStatus(Simulation id);

	@Query(value = "SELECT * FROM simtool.simulation_tasks WHERE simulation_id = :simulationId "
			+ "AND (pv_status IN :status OR agri_status IN :status)", nativeQuery = true)
	List<SimulationTask> getBySimulationAndStatusIn(Long simulationId, List<String> status);

	List<SimulationTask> findBySimulation(Simulation simulation);
	
	@Modifying
	@Query(value = "UPDATE simtool.simulation_tasks " +
	               "SET pv_status = CASE WHEN pv_status = 'QUEUED' THEN 'CANCELLED' ELSE pv_status END, " +
	               "    agri_status = CASE WHEN agri_status = 'QUEUED' THEN 'CANCELLED' ELSE agri_status END " +
	               "WHERE simulation_id = :simulationId " +
	               "AND (pv_status = 'QUEUED' OR agri_status = 'QUEUED')", 
	       nativeQuery = true)
	int updateQueuedStatusToCancelled(Long simulationId);
	
	@Query(value = """
		    WITH task_counts AS (
		        SELECT 
		            st.simulation_id,
		            COUNT(*) FILTER (
		                WHERE 
		                    (s.simulation_type = 'ONLY_PV' AND st.pv_status = 'SUCCESS') OR 
		                    (s.simulation_type = 'ONLY_AGRI' AND st.agri_status = 'SUCCESS') OR 
		                    (s.simulation_type = 'APV' AND st.pv_status = 'SUCCESS')
		            ) AS completed_count
		        FROM simtool.simulation_tasks st
		        JOIN simtool.simulations s ON s.id = st.simulation_id
		        WHERE st.simulation_id = :simulationId
		        GROUP BY st.simulation_id
		    )
		    UPDATE simtool.simulations s
		    SET 
		        completed_task_count = tc.completed_count,
		        status = CASE WHEN tc.completed_count = task_count THEN 'SUCCESS' ELSE status END
		    FROM task_counts tc
		    WHERE s.id = tc.simulation_id;
		""", nativeQuery = true)
	@Modifying
	int updateSimulationCountAndStatusWhileRequequeing(@Param("simulationId") Long simulationId);
	
	@Query(value = """
			    SELECT (pv_status = :queued OR agri_status = :queued)
			    FROM simtool.simulation_tasks
			    WHERE id = :taskId
			""", nativeQuery = true)
	Boolean isQueued(@Param("taskId") Long taskId, @Param("queued") String queued);
	
	@Query(value = """
			WITH updated AS (
			    UPDATE simtool.simulation_tasks
			    SET
			        pv_status = CASE WHEN pv_status = :queued THEN :running ELSE pv_status END,
			        agri_status = CASE WHEN agri_status = :queued THEN :running ELSE agri_status END
			    WHERE id = :taskId
			      AND (:queued IN (pv_status, agri_status))
			    RETURNING 1
			)
			SELECT
			    CASE
			        WHEN EXISTS (SELECT 1 FROM simtool.simulation_tasks WHERE id = :taskId) THEN
			            CASE WHEN EXISTS (SELECT 1 FROM updated) THEN TRUE ELSE FALSE END
			        ELSE FALSE
			    END AS result;
			""", nativeQuery = true)
	Boolean updateQueuedToRunning(@Param("taskId") Long taskId, @Param("queued") String queued,
			@Param("running") String running);


}
