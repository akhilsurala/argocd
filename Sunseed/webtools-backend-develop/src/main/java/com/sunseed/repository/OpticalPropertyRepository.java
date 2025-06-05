package com.sunseed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sunseed.entity.OpticalProperty;

@Repository
public interface OpticalPropertyRepository extends JpaRepository<OpticalProperty, Long> {
	Optional<OpticalProperty> findByOpticalPropertyFileIgnoreCase(String opticalPropertyFile);
}