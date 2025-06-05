package com.sunseed.entity;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sunseed.enums.PreProcessorStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class PvParameter {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Double tiltIfFt;
	private Double maxAngleOfTracking;
	private String moduleMaskPattern;
	private Double gapBetweenModules;
	private Double height;
	private Double xCoordinate;
	private Double yCoordinate;

	@ManyToOne()
	@JoinColumn(name = "moduleType")
	@JsonBackReference
	private PvModule pvModule;

	@ManyToOne
	@JoinColumn(name = "modeOfOperationId")
	@JsonBackReference
	private ModeOfPvOperation modeOfOperationId;

	@ManyToMany
	@JoinTable(name = "pvParameterModuleConfig", joinColumns = @JoinColumn(name = "pvParameterId"), inverseJoinColumns = @JoinColumn(name = "moduleConfigId"))
	@JsonManagedReference
	private List<PvModuleConfiguration> moduleConfig;

	@ManyToOne
	@JoinColumn(name = "projectId")
	@JsonBackReference
	private Projects project;
	
	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	@Enumerated(EnumType.STRING)
	@Column(name="status")
	@Builder.Default
	private PreProcessorStatus status = PreProcessorStatus.DRAFT;
	
	@OneToOne(mappedBy = "pvParameters")
	@JsonBackReference
	private Runs run;

}
