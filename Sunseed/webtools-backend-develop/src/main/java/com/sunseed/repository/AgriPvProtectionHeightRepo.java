package com.sunseed.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.entity.AgriPvProtectionHeight;

public interface AgriPvProtectionHeightRepo extends JpaRepository<AgriPvProtectionHeight, Long> {
List<AgriPvProtectionHeight> findByAgriGeneralParameterId(Long agriGeneralParameterId);
	
}
