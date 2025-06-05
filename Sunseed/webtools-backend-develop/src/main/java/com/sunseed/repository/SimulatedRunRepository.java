package com.sunseed.repository;

import com.sunseed.entity.Runs;
import com.sunseed.entity.SimulatedRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.*;

import java.util.Optional;

public interface SimulatedRunRepository extends JpaRepository<SimulatedRun, Long> {

    Optional<SimulatedRun> findByRun(Runs run);

    @Query("SELECT r FROM SimulatedRun r WHERE r.run.runId IN :runId")
    List<SimulatedRun> getAllSimulatedByRunId(ArrayList<Long> runId);
    
    @Query("SELECT r FROM SimulatedRun r WHERE r.run.runId IN :runId")
    List<SimulatedRun> getAllSimulatedByRunId(List<Long> runId);

    @Query("select s from SimulatedRun s where s.run.runId=:runId")
    SimulatedRun getSimulatedRunByRunId(Long runId);

}
