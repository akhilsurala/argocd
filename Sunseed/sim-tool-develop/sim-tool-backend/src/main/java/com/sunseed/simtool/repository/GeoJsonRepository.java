package com.sunseed.simtool.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sunseed.simtool.entity.GeoJson;
import com.sunseed.simtool.entity.GeoJsonId;

public interface GeoJsonRepository extends JpaRepository<GeoJson, GeoJsonId>{

}
