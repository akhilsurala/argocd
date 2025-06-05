package com.sunseed.simtool.entity;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunseed.simtool.constant.SimulationType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
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
@Table(name = "simulation_blocks")
public class SimulationBlock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Integer blockIndex;
	private LocalDate blockStartDate;
	private LocalDate blockEndDate;
	private LocalDate blockSimulationDate;
	private LocalDate cycleStartDate;
	private Integer cycleDurationInDays;
	private LocalDate cycleEndDate;
	private String cycleName;
	private int runningDaysInBlockForPv;
	
	@Enumerated(EnumType.STRING)
	private SimulationType blockSimulationType;
	

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "simulationBlock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<AgriBlockSimulationDetails> agriBlockSimulationDetails;

	@ToString.Exclude
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "simulation_id")
	private Simulation simulation;

	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "simulationBlock", fetch = FetchType.EAGER)
	private List<SimulationTask> simulationTasks;
	
	@ToString.Exclude
//	@JsonIgnore
	@OneToOne(mappedBy = "simulationBlock", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private SimulationGroundArea simulationGroundArea;
}
