package com.sunseed.simtool.entity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.constant.Status;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@Setter
@Entity
@Table(name = "simulations")
public class Simulation extends BaseEntity{
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long userProfileId;
	private Long projectId;
	private Long runId;
	private Long taskCount;
	private Long completedTaskCount = 0l;
	private Boolean withTracking;
	
	private String comment;
	
	@Enumerated(EnumType.STRING)
	private SimulationType simulationType;
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String,Object> runPayload;
	
	private LocalDate startDate;
	private LocalDate endDate;
	
	@Enumerated(EnumType.STRING)
	private Status status;
	
	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<SimulationTask> simulationTasks;
	
	@ToString.Exclude
	@JsonIgnore
	@OneToMany(mappedBy = "simulation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<SimulationBlock> simulationBlock;
	
//	@Transient
//	private List<SimulationTask> tasks;
	
	@PrePersist
	public void logNewSimAttempt() {
	    log.debug("Attempting to save simulation");
	}
	    
	@PostPersist
	public void logNewSimAdded() {
	    log.debug("Saved simulation: " + id);
	}

	@PreUpdate
	public void logSimUpdateAttempt() {
	    log.debug("Attempting to update simulation: " + id);
	}

	@PostUpdate
	public void logSimUpdate() {
	    log.debug("Updated simulation: " + id);
	}
}