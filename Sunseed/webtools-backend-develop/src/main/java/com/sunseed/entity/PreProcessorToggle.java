package com.sunseed.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.Toggle;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pre_processor_toggle",
	   uniqueConstraints = @UniqueConstraint(columnNames = {"project_id","run_name"})
)
public class PreProcessorToggle {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Enumerated(EnumType.STRING)
	private Toggle toggle;
	
	@Builder.Default
	@Enumerated(EnumType.STRING)
	private PreProcessorStatus preProcessorStatus = PreProcessorStatus.DRAFT;
	
	private String runName;
	private Double lengthOfOneRow;
	private Double pitchOfRows;
	private Double azimuth;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	@ManyToOne
	@JoinColumn(name = "projectId")
	@JsonIgnore
	@JsonBackReference
	private Projects project;
	
	@OneToOne(mappedBy = "preProcessorToggle")
	@JsonIgnore
	@JsonBackReference
	private Runs run;
	
	@ManyToOne
	@JoinColumn(name = "soil_id")
//	@JsonBackReference
	private SoilType soilType;
}
