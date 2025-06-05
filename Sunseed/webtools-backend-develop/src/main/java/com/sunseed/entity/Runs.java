package com.sunseed.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sunseed.enums.RunStatus;

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
@Table(name = "user_run",
       uniqueConstraints = @UniqueConstraint(columnNames = {"project_id","run_name"})
)
@Builder
public class Runs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long runId;

    private String runName;

    @Builder.Default
    private boolean isSimulated = false;

    @Builder.Default
    private boolean isActive = true;

    @Builder.Default
    private boolean canSimulate = true;
    
    private Long cloneId;
    
    @Builder.Default
    private boolean isMaster = true;
    
    @Builder.Default
    private boolean agriControl = false;
    
    @Builder.Default
    private boolean pvControl = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Projects inProject;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "preProcessorToggleId", nullable = false)
    @JsonManagedReference
    private PreProcessorToggle preProcessorToggle;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pv_parameter_id", nullable = true)
    @JsonManagedReference
    @JsonIgnore
    private PvParameter pvParameters;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "crop_parameters_id", nullable = true)
    @JsonManagedReference
    private CropParameters cropParameters;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "agri_general_parameters_id", nullable = true)
    @JsonManagedReference
    private AgriGeneralParameter agriGeneralParameters;

    @OneToOne(optional = true, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @JoinColumn(name = "economic_parameter_id", nullable = true)
    @JsonManagedReference
    private EconomicParameters economicParameters;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private RunStatus runStatus;


    @OneToOne(mappedBy = "run", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private SimulatedRun simulatedRun;

}
