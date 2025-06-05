package com.sunseed.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.modelmapper.internal.bytebuddy.dynamic.loading.InjectionClassLoader;

import java.time.Instant;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "economic_multicrop")
public class EconomicMultiCrop {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ton per acre
    @ManyToOne
    @JoinColumn(name = "crop_id", referencedColumnName = "id")
    @JsonBackReference
    private Crop crop;
    @Column(name = "min_reference_yield")
    private Double minReferenceYieldCost;

    @Column(name = "max_reference_yield")
    private Double maxReferenceYieldCost;

    // rs per acre
    @Column(name = "min_input_cost_of_crop")
    private Double minInputCostOfCrop;
    @Column(name = "max_input_cost_of_crop")
    private Double maxInputCostOfCrop;

    // rs per kg
    @Column(name = "min_selling_cost_of_crop")
    private Double minSellingCostOfCrop;
    @Column(name = "max_selling_cost_of_crop")
    private Double maxSellingCostOfCrop;

    @Column(name = "cultivation_area")
    private Double cultivationArea;

    @ManyToOne
    @JoinColumn(name = "economic_parameters_id")
    @JsonBackReference
    private EconomicParameters economicParameters;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;

}
