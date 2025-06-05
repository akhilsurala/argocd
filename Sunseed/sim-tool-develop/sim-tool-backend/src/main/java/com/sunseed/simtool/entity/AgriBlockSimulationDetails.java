package com.sunseed.simtool.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "agri_block_simulation_details")
public class AgriBlockSimulationDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private int runningDaysInBlock; // running days for crop in a particular block
	private int cropAge; // total cropAge that the crop has matured
	private Integer bedIndex;
	private String bedName;
	private String cropName;
	private LocalDate cropStartDate;
	private LocalDate cropEndDate;  // cropStartDate + duration
	private int duration;
	private int minStage;
	private int maxStage;
	
	@Transient
	private int o1;
	@Transient
	private int o2;
	@Transient
	private int s1;
	@Transient
	private double reflectivity_NIR;
	@Transient
	private double reflectivity_PAR;
	@Transient
	private double transmissivity_NIR;
	@Transient
	private double transmissivity_PAR;
	
	@JsonIgnore
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "simulation_block_id")
	private SimulationBlock simulationBlock;
}
