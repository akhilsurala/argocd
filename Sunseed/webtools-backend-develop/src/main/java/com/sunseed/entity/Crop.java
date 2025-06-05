package com.sunseed.entity;

import java.time.Instant;
import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Crop {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; 
	
	private String name;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "optical_property_id")
//	@JsonBackReference
	@JsonManagedReference
    private OpticalProperty opticalProperty;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "stomatal_parameter_id")
//	@JsonBackReference
	@JsonManagedReference
    private StomatalParameter stomatalParameter;
	
	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "farquhar_parameter_id")
//	@JsonBackReference
	@JsonManagedReference
    private FarquharParameter farquharParameter;
	
	@NotNull
    @Column(name = "required_dli")
    private Long requiredDLI;
	
	@NotNull
    @Column(name = "required_ppfd")
    private Long requiredPPFD;

    @NotNull
    @Column(name = "harvest_days")
    private Long harvestDays;
    
    @Column(name = "f1")
    private Double f1;

    @Column(name = "f2")
    private Double f2;

    @Column(name = "f3")
    private Double f3;

    @Column(name = "f4")
    private Double f4;

    @Column(name = "f5")
    private Double f5;
    
    @Column(name = "min_stage")
    private Long minStage;

    @Column(name = "max_stage")
    private Long maxStage;

//    @Column(name = "duration")
//    @Min(value = 1, message = "duration.must.be.between.1.and.365")
//    @Max(value = 365, message = "duration.must.be.between.1.and.365")
//    private Long durationStage;


	// totalstagingCount, duration
	@Column(name="total_staging_count")
	private Integer totalStagingCount;

	private Integer duration;
	
	@Builder.Default
	private Boolean isActive = true;
	
	@Builder.Default
	private Boolean hide = true;
	
	@CreationTimestamp
	private Instant createdAt;
	
	@UpdateTimestamp
	private Instant updatedAt;
	
	@Column(name = "crop_label")
	private String cropLabel;
	
	@Column(name = "has_plant_actual_date", nullable = false)
	@Builder.Default
    private Boolean hasPlantActualDate = false;

    @Column(name = "plant_actual_start_date")
    private String plantActualStartDate;

    @Column(name = "plant_max_age", nullable = false)
    private Integer plantMaxAge;

    @Column(name = "max_plants_per_bed", nullable = false)
    private Integer maxPlantsPerBed;


}
