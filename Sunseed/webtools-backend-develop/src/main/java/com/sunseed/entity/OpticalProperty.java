package com.sunseed.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "optical_property")
public class OpticalProperty {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "transmission_nir") // Maps to transmission_nir in the database
	private Double transmissionNIR;

	@NotNull
	@Column(name = "reflection_nir") // Maps to reflection_nir in the database
	private Double reflectionNIR;

	@NotNull
	@Column(name = "transmission_par") // Maps to transmission_par in the database
	private Double transmissionPAR;

	@NotNull
	@Column(name = "reflection_par") // Maps to reflection_par in the database
	private Double reflectionPAR;
	
	@Column(name = "optical_property_file") // New column for the optical property file
    private String opticalPropertyFile;
	@Column(name = "link_to_texture")
	private String linkToTexture;
	
	@Column(name = "master_type")
	private String masterType;
	
	@Column(name = "master_id")
	private Long masterId;

	@OneToOne(mappedBy = "opticalProperty", fetch = FetchType.LAZY)
    @JsonBackReference  // Prevent circular reference
    private Crop crop;
}