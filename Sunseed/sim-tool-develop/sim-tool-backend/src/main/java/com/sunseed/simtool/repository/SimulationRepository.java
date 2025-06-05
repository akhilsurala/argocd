package com.sunseed.simtool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.simtool.entity.Simulation;

public interface SimulationRepository extends JpaRepository<Simulation, Long> {

	Optional<Simulation> findByRunId(Long runId);

}
