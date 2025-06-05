package com.sunseed.simtool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunseed.simtool.constant.LeafType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "crop_yields")
@NoArgsConstructor
@AllArgsConstructor
public class CropYield extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "simulation_task_id")
	private SimulationTask simulationTask;
	private Integer bedIndex;
	private String cropName;
	
	@Enumerated(EnumType.STRING)
	private LeafType leafType;
	
	private Float carbonAssimilation;
	private Float saturation;
	private Float temperature;
	private Float radiation;
	private Float latentFlux;
	private Float leavesArea;
	private Float penetration;
	private Integer cropCount;
	private Float saturationExtent;
	
}
