package com.sunseed.entity;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.sunseed.enums.PreProcessorStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "economic_parameters")
public class EconomicParameters {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long economicId;

    @ManyToOne
    @JoinColumn(name = "currency_id")
    @JsonBackReference
    private Currency currency;

    @Column(name = "economic_parameter")
    private boolean economicParameter;

    @OneToMany(mappedBy = "economicParameters", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<EconomicMultiCrop> economicMultiCrop;
    // kwatt
//	@Column(name="min_selling_point_of_power")
//	private Integer minSellingPointOfPower;
//	@Column(name="max_selling_point_of_power")
//	private Integer maxSellingPointOfPower;

    @Column(name = "hourly_selling_rates", columnDefinition = "DOUBLE PRECISION[]")
    @Builder.Default
    private Double[] hourlySellingRates = new Double[24];

    @ManyToOne
    @JoinColumn(name = "project_id")
    @JsonBackReference
    private Projects project;

    @OneToOne(mappedBy = "economicParameters")
    @JsonBackReference
    private Runs run;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PreProcessorStatus status = PreProcessorStatus.DRAFT;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
