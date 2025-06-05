package com.sunseed.simtool.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.simtool.entity.E2EMachineSpecifications;

public interface E2EMachineSpecificationsRepository extends JpaRepository<E2EMachineSpecifications, Long>{

	Optional<E2EMachineSpecifications> findByPlan(String planId);

}
