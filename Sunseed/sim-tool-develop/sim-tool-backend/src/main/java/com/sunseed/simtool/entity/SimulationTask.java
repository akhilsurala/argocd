package com.sunseed.simtool.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "simulation_tasks", uniqueConstraints = { @UniqueConstraint(columnNames = { "simulation_id", "date" }) })
public class SimulationTask extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
//	@JsonIgnore
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "simulation_id")
	private Simulation simulation;
	
	private LocalDateTime enqueuedAt;
	private LocalDateTime date;
	private String taskTimezone;
	
	@Enumerated(EnumType.STRING)
	private Status pvStatus;
	@Enumerated(EnumType.STRING)
	private Status agriStatus;
	
	private LocalDateTime completedAt;
	private Long taskExecutionTimeOnServer;
	private String serverName;
	
	@Builder.Default
	private Integer cpuRequired = 1;
	
	@Builder.Default
	private Double ramRequired = 1.0; // in GB
	
	@JdbcTypeCode(SqlTypes.JSON)
	@Column(columnDefinition = "jsonb")
	private Map<String,String> weatherCondition;
	
	@OneToOne(mappedBy = "simulationTask", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private PVYield pvYields;
	@OneToOne(mappedBy = "simulationTask", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
	private TrackingTiltAngle trackingTiltAngles;
	@OneToMany(mappedBy = "simulationTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Scene> scenes;
	@OneToMany(mappedBy = "simulationTask", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<CropYield> cropYields;
	
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "simulation_block_id")
	private SimulationBlock simulationBlock;
	
	@Transient
	private boolean isFirst = false;
	
	@Builder.Default
	private boolean hasHighestRadiation = false;
	
//	@PrePersist
//	public void logNewSimAttempt() {
//	    log.debug("Attempting to save simulation task");
//	}
//	    
//	@PostPersist
//	public void logNewSimAdded() {
//	    log.debug("Saved simulation task: " + id);
//	}
//
//	@PreUpdate
//	public void logSimUpdateAttempt() {
//	    log.debug("Attempting to update simulation task: " + id);
//	}
//
//	@PostUpdate
//	public void logSimUpdate() {
//	    log.debug("Updated simulation task: " + id);
//	}
}
