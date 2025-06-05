package com.sunseed.entity;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class BedParameter {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double bedWidth;
    private Double bedHeight;
    private Double bedAngle;
    private Double bedAzimuth;
    private Double bedcc;
    private Double startPointOffset;

    @OneToOne
    @JoinColumn(name = "agri_general_parameter")
    @JsonBackReference
    @JsonIgnoreProperties("bedParameter")
    private AgriGeneralParameter agriGeneralParameter;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;


}
