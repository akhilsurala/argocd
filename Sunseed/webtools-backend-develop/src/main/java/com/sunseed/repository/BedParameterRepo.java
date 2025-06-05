package com.sunseed.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.entity.AgriGeneralParameter;
import com.sunseed.entity.BedParameter;

public interface BedParameterRepo extends JpaRepository<BedParameter, Long>{

	Optional<BedParameter> findByAgriGeneralParameter(AgriGeneralParameter agriGeneralParameter);

}
