package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.Runs;
import com.sunseed.enums.RunStatus;
import com.sunseed.model.responseDTO.RunNameListResponseDto;
import com.sunseed.projection.DeleteRunProjection;
import com.sunseed.projection.RunDesignExplorerProjection;
import com.sunseed.projection.RunProjectionRecord;
import com.sunseed.report.RunProjectionForReport;

@Repository
public interface RunsRepository extends JpaRepository<Runs, Long> {

    @Query("SELECT r FROM Runs r WHERE r.inProject.id = :projectId AND r.runStatus IN :runStatusList ORDER BY r.updatedAt DESC")
    List<Runs> getAllRunsForBay(Long projectId, List<RunStatus> runStatusList);

    @Query("SELECT r FROM Runs r WHERE r.id = :id AND r.runStatus = 'HOLDING' ORDER BY r.updatedAt DESC")
    Runs getRunInHoldingByRunId(Long id);

    //  @Query("SELECT r FROM Runs r WHERE r.inProject.id = :projectId AND r.runStatus IN :runStatusList AND r.isMaster = true ORDER BY r.updatedAt DESC")

    @Query("SELECT r FROM Runs r " +
            "WHERE r.isActive= true AND r.inProject.id = :projectId " +
            "AND (" +
            "  (r.isMaster = true AND r.runStatus IN :runStatusList) " +
            "  OR " +
            "  (r.isMaster = true AND EXISTS (" +
            "      SELECT v FROM Runs v WHERE v.isActive=true AND v.cloneId = r.runId AND v.runStatus IN :runStatusList" +
            "    )" +
            "  )" +
            ") " +
            "ORDER BY r.createdAt DESC")
    List<Runs> getAllRunsForRunningWithMasterTrueAndVarientMaster(Long projectId, List<RunStatus> runStatusList);

    @Query("SELECT r from Runs r WHERE r.isActive=true AND r.cloneId = :masterRunId ORDER BY r.createdAt DESC")
    List<Runs> getVariantRuns(Long masterRunId);

    @Query(value = "SELECT new com.sunseed.projection.RunProjectionRecord(r1.runId,r1.runName,r1.inProject.projectId,r1.preProcessorToggle, "
            + "r1.pvParameters,r1.cropParameters, r1.agriGeneralParameters, r1.economicParameters, r1.runStatus,r1.simulatedRun,r1.createdAt,r1.updatedAt,r1.cloneId,r1.isMaster, "
            + "r1.agriControl, r1.pvControl, "
            + "(CASE WHEN COUNT(v.runId) > 0 THEN true ELSE false END) as variantExist) " + "FROM Runs r1 "
            + "LEFT JOIN Runs v ON v.cloneId = r1.runId " + "LEFT JOIN r1.inProject "
            + "LEFT JOIN r1.preProcessorToggle " + "LEFT JOIN r1.pvParameters " + "LEFT JOIN r1.cropParameters "
            + "LEFT JOIN r1.agriGeneralParameters " + "LEFT JOIN r1.economicParameters " + "LEFT JOIN r1.simulatedRun "
            + "WHERE r1.inProject.projectId =:projectId AND r1.isMaster=true AND r1.isActive=true AND r1.runStatus IN :runStatusList "
            + "GROUP BY r1.runId,r1.runName,r1.inProject,r1.preProcessorToggle,r1.pvParameters,r1.cropParameters,r1.agriGeneralParameters, "
            + "r1.economicParameters,r1.runStatus,r1.simulatedRun,r1.createdAt,r1.updatedAt,r1.cloneId,r1.isMaster,r1.agriControl,r1.pvControl "
            + "ORDER BY r1.createdAt DESC")
    List<RunProjectionRecord> findMasterRunsWithVariantExist(Long projectId, List<RunStatus> runStatusList);

//	@Query(value = "SELECT new com.sunseed.projection.RunProjectionRecord(r1.runId,r1.runName,r1.inProject.projectId,r1.preProcessorToggle, "
//			+ "r1.pvParameters,r1.cropParameters, r1.agriGeneralParameters, r1.economicParameters, r1.runStatus,r1.simulatedRun,r1.createdAt,r1.updatedAt,r1.cloneId,r1.isMaster, "
//			+ "r1.agriControl,r1.pvControl, "
//			+ "(CASE WHEN COUNT(v.runId) > 0 THEN true ELSE false END) as variantExist) " + "FROM Runs r1 "
//			+ "LEFT JOIN Runs v ON v.cloneId = r1.runId " + "LEFT JOIN r1.inProject "
//			+ "LEFT JOIN r1.preProcessorToggle " + "LEFT JOIN r1.pvParameters " + "LEFT JOIN r1.cropParameters "
//			+ "LEFT JOIN r1.agriGeneralParameters " + "LEFT JOIN r1.economicParameters " + "LEFT JOIN r1.simulatedRun "
//			+ "WHERE r1.inProject.projectId =:projectId AND LOWER(r1.runName) LIKE LOWER(CONCAT('%', :searchText, '%')) AND r1.isMaster=true AND r1.runStatus IN :runStatusList "
//			+ "GROUP BY r1.runId,r1.runName,r1.inProject,r1.preProcessorToggle,r1.pvParameters,r1.cropParameters,r1.agriGeneralParameters, "
//			+ "r1.economicParameters,r1.runStatus,r1.simulatedRun,r1.createdAt,r1.updatedAt,r1.cloneId,r1.isMaster,r1.agriControl,r1.pvControl "
//			+ "ORDER BY r1.updatedAt DESC")

    @Query(value = "SELECT new com.sunseed.projection.RunProjectionRecord( "
            + "r1.runId, r1.runName, r1.inProject.projectId, r1.preProcessorToggle, "
            + "r1.pvParameters, r1.cropParameters, r1.agriGeneralParameters, r1.economicParameters, "
            + "r1.runStatus, r1.simulatedRun, r1.createdAt, r1.updatedAt, r1.cloneId, r1.isMaster, "
            + "r1.agriControl, r1.pvControl, "
            + "(CASE "
            + "    WHEN r1.isMaster = true THEN "
            + "        CASE WHEN COUNT(v.runId) > 0 THEN true "
            + "             ELSE false "
            + "        END "
            + "    ELSE false "
            + " END) as variantExist) "
            + "FROM Runs r1 "
            + "LEFT JOIN Runs v ON v.cloneId = r1.runId "
            + "LEFT JOIN r1.inProject "
            + "LEFT JOIN r1.preProcessorToggle "
            + "LEFT JOIN r1.pvParameters "
            + "LEFT JOIN r1.cropParameters "
            + "LEFT JOIN r1.agriGeneralParameters "
            + "LEFT JOIN r1.economicParameters "
            + "LEFT JOIN r1.simulatedRun "
            + "WHERE r1.inProject.projectId = :projectId "
            + "AND ("
            + "    (r1.isMaster = true AND LOWER(r1.runName) LIKE LOWER(CONCAT('%', :searchText, '%'))) "
            + "    OR ("
            + "        r1.isMaster = true AND r1.runId IN ("
            + "            SELECT r2.cloneId FROM Runs r2 "
            + "            WHERE r2.isMaster = false "
            + "            AND LOWER(r2.runName) LIKE LOWER(CONCAT('%', :searchText, '%'))"
            + "        )"
            + "    )"
            + ") "
            + "AND r1.isActive=true AND r1.runStatus IN :runStatusList "
            + "GROUP BY r1.runId, r1.runName, r1.inProject, r1.preProcessorToggle, r1.pvParameters, "
            + "r1.cropParameters, r1.agriGeneralParameters, r1.economicParameters, r1.runStatus, r1.simulatedRun, "
            + "r1.createdAt, r1.updatedAt, r1.cloneId, r1.isMaster, r1.agriControl, r1.pvControl "
            + "ORDER BY r1.createdAt DESC")
    List<RunProjectionRecord> findMasterRunsWithVariantExistAndSearchText(Long projectId, List<RunStatus> runStatusList,
                                                                          String searchText);

    @Query("SELECT new com.sunseed.projection.RunProjectionRecord( "
            + "r1.runId, r1.runName, r1.inProject.projectId, r1.preProcessorToggle, "
            + "r1.pvParameters, r1.cropParameters, r1.agriGeneralParameters, "
            + "r1.economicParameters, r1.runStatus, r1.simulatedRun, r1.createdAt, "
            + "r1.updatedAt, r1.cloneId, r1.isMaster,r1.agriControl,r1.pvControl, "
            + "(CASE WHEN r1.isMaster = true THEN (CASE WHEN COUNT(v.runId) > 0 THEN true ELSE false END) ELSE false END) as variantExist "
            + ") " + "FROM Runs r1 " + "LEFT JOIN Runs v ON v.cloneId = r1.runId " + "LEFT JOIN r1.inProject "
            + "LEFT JOIN r1.preProcessorToggle " + "LEFT JOIN r1.pvParameters " + "LEFT JOIN r1.cropParameters "
            + "LEFT JOIN r1.agriGeneralParameters " + "LEFT JOIN r1.economicParameters " + "LEFT JOIN r1.simulatedRun "
            + "WHERE r1.inProject.projectId =:projectId AND r1.runId = :runId AND r1.isMaster = true AND r1.cloneId IS NULL AND r1.runStatus IN :runStatusList "
            + "OR r1.inProject.projectId =:projectId AND r1.cloneId = :runId AND r1.runStatus IN :runStatusList "
            + "GROUP BY r1.runId, r1.runName, r1.inProject.projectId, r1.preProcessorToggle, "
            + "r1.pvParameters, r1.cropParameters, r1.agriGeneralParameters, "
            + "r1.economicParameters, r1.runStatus, r1.simulatedRun, "
            + "r1.createdAt, r1.updatedAt, r1.cloneId, r1.isMaster,r1.agriControl,r1.pvControl "
            + "ORDER BY r1.createdAt DESC")
    List<RunProjectionRecord> findMasterAndVariantsRuns(Long projectId, Long runId, List<RunStatus> runStatusList);

    @Query("SELECT new com.sunseed.projection.DeleteRunProjection( " + "r.runId, r.runStatus, r.isMaster, "
            + "(CASE WHEN r.isMaster = true THEN (CASE WHEN COUNT(v.runId) > 0 THEN true ELSE false END) ELSE false END) as variantExist "
            + ") " + "FROM Runs r " + "LEFT JOIN Runs v ON v.cloneId = r.runId " + "WHERE r.runId = :runId "
            + "GROUP BY r.runId, r.runStatus, r.isMaster")
    Optional<DeleteRunProjection> findRunForDelete(Long runId);

    @Query("SELECT new com.sunseed.projection.RunProjectionRecord( "
            + "r1.runId, r1.runName, r1.inProject.projectId, r1.preProcessorToggle, "
            + "r1.pvParameters, r1.cropParameters, r1.agriGeneralParameters, "
            + "r1.economicParameters, r1.runStatus, r1.simulatedRun, r1.createdAt, "
            + "r1.updatedAt, r1.cloneId, r1.isMaster,r1.agriControl,r1.pvControl, "
            + "(CASE WHEN r1.isMaster = true THEN (CASE WHEN COUNT(v.runId) > 0 THEN true ELSE false END) ELSE false END) as variantExist "
            + ") " + "FROM Runs r1 " + "LEFT JOIN Runs v ON v.cloneId = r1.runId " + "LEFT JOIN r1.inProject "
            + "LEFT JOIN r1.preProcessorToggle " + "LEFT JOIN r1.pvParameters " + "LEFT JOIN r1.cropParameters "
            + "LEFT JOIN r1.agriGeneralParameters " + "LEFT JOIN r1.economicParameters " + "LEFT JOIN r1.simulatedRun "
            + "WHERE r1.inProject.projectId =:projectId AND r1.isActive=true AND r1.runStatus IN :runStatusList "
            + "GROUP BY r1.runId, r1.runName, r1.inProject.projectId, r1.preProcessorToggle, "
            + "r1.pvParameters, r1.cropParameters, r1.agriGeneralParameters, "
            + "r1.economicParameters, r1.runStatus, r1.simulatedRun, "
            + "r1.createdAt, r1.updatedAt, r1.cloneId, r1.isMaster,r1.agriControl,r1.pvControl "
            + "ORDER BY r1.createdAt DESC")
    List<RunProjectionRecord> findAllRunsForRunningBay(Long projectId, List<RunStatus> runStatusList);

    @Modifying
    @Query("UPDATE Runs r SET r.pvControl = false WHERE r.pvControl = true AND (r.runId = :masterRunId OR r.cloneId = :masterRunId)")
    int updatePvControlInGroupToFalse(Long masterRunId);

    @Modifying
    @Query("UPDATE Runs r SET r.agriControl = false WHERE r.agriControl = true AND (r.runId = :masterRunId OR r.cloneId = :masterRunId)")
    int updateAgriControlInGroupToFalse(Long masterRunId);

//	@Query("""
//			    SELECT new com.sunseed.projection.RunDesignExplorerProjection(
//			        r.runId,
//			        r.runName,
//			        r.inProject.projectId,
//			        r.preProcessorToggle,
//			        r.pvParameters,
//			        r.cropParameters,
//			        r.agriGeneralParameters,
//			        r.economicParameters,
//			        r.runStatus,
//			        r.simulatedRun,
//			        r.createdAt,
//			        r.updatedAt,
//			        r.cloneId,
//			        r.isMaster,
//			        r.agriControl,
//			        r.pvControl
//			    )
//			    FROM Runs r
//			    LEFT JOIN r.inProject
//			    LEFT JOIN r.preProcessorToggle
//			    LEFT JOIN r.pvParameters
//			    LEFT JOIN r.cropParameters
//			    LEFT JOIN r.agriGeneralParameters
//			    LEFT JOIN r.economicParameters
//			    LEFT JOIN r.simulatedRun
//
//			    WHERE r.runId IN :runIds AND
//			    r.inProject.projectId = :projectId AND
//			    r.runStatus = :runStatus AND
//			   (
//			     /* Ensure exactly one master run exists in the list */
//			     (SELECT COUNT(master.runId)
//			      FROM Runs master
//			      WHERE master.runId IN :runIds
//			      AND master.isMaster = true) = 1
//			   ) 
//			   AND
//			   (
//			     /* Ensure each run is either the master or its valid variant */
//			     r.isMaster = true
//			     OR r.cloneId = (
//			         SELECT master.runId
//			         FROM Runs master
//			         WHERE master.runId IN :runIds
//			         AND master.isMaster = true
//			     )
//			   )
//			   AND NOT EXISTS 
//			   (
//			     /* Ensure no invalid variants (runs not belonging to the master group) */
//			     SELECT 1
//			     FROM Runs invalidRun
//			     WHERE invalidRun.runId IN :runIds
//			     AND invalidRun.isMaster = false
//			     AND invalidRun.cloneId NOT IN (
//			         SELECT master.runId
//			         FROM Runs master
//			         WHERE master.runId IN :runIds
//			         AND master.isMaster = true
//			     )
//			   )
//			   GROUP BY r.runId, r.runName, r.inProject.projectId, r.preProcessorToggle,
//               r.pvParameters, r.cropParameters, r.agriGeneralParameters,
//               r.economicParameters, r.runStatus, r.simulatedRun,
//               r.createdAt, r.updatedAt, r.cloneId, r.isMaster,
//               r.agriControl, r.pvControl
//			""")

    @Query("SELECT new com.sunseed.projection.RunDesignExplorerProjection( "
            + "r.runId, r.runName, r.inProject.projectId, r.preProcessorToggle, "
            + "r.pvParameters, r.cropParameters, r.agriGeneralParameters, "
            + "r.economicParameters, r.runStatus, r.simulatedRun, r.createdAt, "
            + "r.updatedAt, r.cloneId, r.isMaster, r.agriControl, r.pvControl) "
            + "FROM Runs r "
            + "LEFT JOIN r.inProject "
            + "LEFT JOIN r.preProcessorToggle "
            + "LEFT JOIN r.pvParameters "
            + "LEFT JOIN r.cropParameters "
            + "LEFT JOIN r.agriGeneralParameters "
            + "LEFT JOIN r.economicParameters "
            + "LEFT JOIN r.simulatedRun "
            + "WHERE r.runId IN :runIds AND "
            + "r.inProject.projectId = :projectId AND "
            + "r.runStatus = :runStatus AND "
            + "(SELECT COUNT(master.runId) "
            + " FROM Runs master "
            + " WHERE master.runId IN :runIds AND master.isMaster = true) = 1 AND "
            + "(r.isMaster = true OR r.cloneId = ( "
            + " SELECT master.runId "
            + " FROM Runs master "
            + " WHERE master.runId IN :runIds AND master.isMaster = true)) AND "
            + "NOT EXISTS ( "
            + " SELECT 1 "
            + " FROM Runs invalidRun "
            + " WHERE invalidRun.runId IN :runIds AND "
            + " invalidRun.isMaster = false AND "
            + " invalidRun.cloneId NOT IN ( "
            + " SELECT master.runId "
            + " FROM Runs master "
            + " WHERE master.runId IN :runIds AND master.isMaster = true)) "
            + "GROUP BY r.runId, r.runName, r.inProject.projectId, r.preProcessorToggle, "
            + "r.pvParameters, r.cropParameters, r.agriGeneralParameters, "
            + "r.economicParameters, r.runStatus, r.simulatedRun, "
            + "r.createdAt, r.updatedAt, r.cloneId, r.isMaster, "
            + "r.agriControl, r.pvControl")
    List<RunDesignExplorerProjection> getRunDetailsListForDesignExplorer(@Param("projectId") Long projectId,
                                                                         @Param("runStatus") RunStatus runStatus, @Param("runIds") List<Long> runIdList);

    //    @Query(value = "SELECT new com.sunseed.model.responseDTO.RunNameListResponseDto("
//            + "r.runId, r.runName) "
//            + "From Runs r "
//            + "WHERE r.inProject.projectId = :projectId "
//            + "AND r.runId IN :runIds")
    @Query("SELECT new com.sunseed.model.responseDTO.RunNameListResponseDto("
            + "r.runId, r.runName, r.preProcessorToggle.toggle) "
            + "FROM Runs r "
            + "JOIN r.preProcessorToggle pt "
            + "WHERE r.inProject.projectId = :projectId "
            + "AND r.runId IN :runIds")
    List<RunNameListResponseDto> getRunNames(@Param("projectId") Long projectId, @Param("runIds") List<Long> runIdList);
    
    @Query("SELECT new com.sunseed.report.RunProjectionForReport(" +
    	       "r.id, r.inProject.projectId, r.runName, r.preProcessorToggle, r.pvParameters, r.cropParameters, " +
    	       "r.runStatus, r.simulatedRun, r.createdAt, r.updatedAt, r.agriGeneralParameters, r.economicParameters) " +
    	       "FROM Runs r " +
    	       "LEFT JOIN r.preProcessorToggle pt " +
    	       "LEFT JOIN r.pvParameters pv " +
    	       "LEFT JOIN r.cropParameters cp " +
    	       "LEFT JOIN r.simulatedRun sr " +
    	       "LEFT JOIN r.agriGeneralParameters ag " +
    	       "LEFT JOIN r.economicParameters ec " +
    	       "WHERE r.inProject.projectId = :projectId " +
    	       "AND r.id IN :runIds")
    List<RunProjectionForReport> getRunProjectionsForReport(@Param("projectId") Long projectId, @Param("runIds") List<Long> runIds);
    
	@Query(value = """
				    SELECT r.*
			FROM user_run r
			JOIN simulated_runs sr ON r.run_id = sr.run_id
			JOIN simtool.simulations sim ON sr.simulated_id = sim.id
			WHERE r.status = :runStatus
			  AND r.is_active = true
			  AND sim.status = 'SUCCESS'
			  OR sim.status = 'FAILED'
			ORDER BY r.updated_at DESC
				""", nativeQuery = true)
    	List<Runs> getRunsWithCompletedSimulationsByStatus(@Param("runStatus") String runStatus);






    @Query(value = """
    WITH master_run AS (
        SELECT COALESCE(r.clone_id, r.run_id) AS master_id
        FROM public.user_run r
        WHERE r.run_id IN (:runIds)
        LIMIT 1
    ),
    all_runs AS (
        SELECT r.run_id
        FROM public.user_run r
        WHERE (r.clone_id = (SELECT master_id FROM master_run) OR r.run_id = (SELECT master_id FROM master_run))
        AND r.agri_control = true
    )
    SELECT run_id 
    FROM all_runs 
    LIMIT 1;
""", nativeQuery = true)
    Optional<Long> findAgriControlRunId(@Param("runIds") List<Long> runIds);

}
