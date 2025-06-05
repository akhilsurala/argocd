package com.sunseed.entity;

import java.time.Instant;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sunseed.enums.PreProcessorStatus;
import com.sunseed.enums.TempControl;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class AgriGeneralParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	private TempControl tempControl;

	private Double trail;
	@Column(name="min_temp")
	private Double minTemp;
	@Column(name="max_temp")
	private Double maxTemp;

	private Boolean isMulching;
	
    @Builder.Default
    @Enumerated(EnumType.STRING)
	private PreProcessorStatus status=PreProcessorStatus.DRAFT;
    
    @CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	@OneToMany(mappedBy = "agriGeneralParameter", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<AgriPvProtectionHeight> agriPvProtectionHeight;

	@ManyToOne
	@JoinColumn(name = "irrigation_id")
	@JsonBackReference
	private Irrigation irrigationId;

//	@ManyToOne
//	@JoinColumn(name = "soil_id")
//	@JsonBackReference
//	private SoilType soilType;
	
	@OneToOne(mappedBy = "agriGeneralParameter", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private BedParameter bedParameter;
	
    @ManyToOne
    @JoinColumn(name = "project_id")
	@JsonBackReference
	private Projects project;
	
	@OneToOne(mappedBy = "agriGeneralParameters")
	@JsonBackReference
	private Runs run;

}
