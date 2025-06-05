package com.sunseed.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.Projects;
import com.sunseed.enums.RunStatus;
import com.sunseed.report.ProjectDetail;

import jakarta.persistence.Tuple;

@Repository
public interface ProjectsRepository extends JpaRepository<Projects, Long> {

    @Query("SELECT p.projectId FROM Projects p WHERE p.userProfile.userProfileId = :userProfileId AND p.id = :projectId")
    Long existsByUserProfileAndProjectId(Long userProfileId, Long projectId);

    @Query("SELECT p FROM Projects p WHERE p.userProfile.userProfileId = :userProfileId AND LOWER(p.projectName) = LOWER(:projectName)")
    Optional<Projects> existsByUserProfileAndProjectName(Long userProfileId, String projectName);

    @Query("SELECT COUNT(r) FROM Runs r WHERE r.inProject.projectId = :projectId")
    long countRunsByProjectId(Long projectId);

    @Query("Select p.projectId from Projects p where p.userProfile.userProfileId=:profileId and Lower(p.projectName)=Lower(:projectName)")
    Long findIdWithUserProfileAndProjectNameIgnoreCase(Long profileId, String projectName);

    @Query(value = """
            SELECT p.project_id AS projectId,
                   p.project_name AS projectName,
                   p.latitude,
                   p.longitude,
                   p.created_at AS createdOn,
                   p.updated_at AS lastEdited,
                   p.offset_point As offsetPoint,
                   COUNT(r.run_id) AS numberOfRuns,
                   p.comments,
                   p.run_ids AS runIds,
                   NULL AS location
            FROM projects p
            LEFT JOIN user_run r ON p.project_id = r.project_id
            WHERE p.user_profile_id = :userProfileId
            AND p.project_status = 'ACTIVE'
              AND LOWER(p.project_name) LIKE LOWER(CONCAT('%', :searchText, '%'))
            GROUP BY p.project_id, p.project_name, p.latitude, p.longitude, p.created_at, p.updated_at, p.comments
            """, nativeQuery = true)
    List<Tuple> findProjectsWithRunCountByUserProfileIdAndSearchText(Long userProfileId,
                                                                     String searchText);

    @Query(value = """
            SELECT p.project_id AS projectId,
                   p.project_name AS projectName,
                   p.latitude,
                   p.longitude,
                   p.created_at AS createdOn,
                   p.updated_at AS lastEdited,
                   p.offset_point As offsetPoint,
                   COUNT(r.run_id) AS numberOfRuns,
                   p.comments,
                   p.run_ids AS runIds,
                   NULL AS location
            FROM projects p
            LEFT JOIN user_run r ON p.project_id = r.project_id
            WHERE p.user_profile_id = :userProfileId 
              AND p.project_status = 'ACTIVE'
            GROUP BY p.project_id, p.project_name, p.latitude, p.longitude, p.created_at, p.updated_at, p.comments
            """, nativeQuery = true)
    List<Tuple> findProjectsWithRunCountByUserProfileId(Long userProfileId);
    
    @Query(value = "SELECT new com.sunseed.report.ProjectDetail(" + "p.projectId," + "p.projectName," + "p.latitude,"
			+ "p.longitude," + "p.createdAt," + "p.updatedAt,"
			+ "SUM(CASE WHEN r.runStatus IN :holdingBayRunStatusList THEN 1 ELSE 0 END) as runInHoldingBay,"
			+ "SUM(CASE WHEN r.runStatus IN :runningBayRunStatusList THEN 1 ELSE 0 END) as runInRunningBay" + ") "
			+ "FROM Projects p " + "LEFT JOIN p.runs r " + "WHERE p.projectId = :projectId "
			+ "GROUP BY p.projectId, p.projectName, p.latitude, p.longitude, p.createdAt, p.updatedAt")
	Optional<ProjectDetail> getProjectDetailWithNoOfRun(Long projectId, List<RunStatus> holdingBayRunStatusList,
			List<RunStatus> runningBayRunStatusList);
}
