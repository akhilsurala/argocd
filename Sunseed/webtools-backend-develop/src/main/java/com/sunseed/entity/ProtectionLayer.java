package com.sunseed.entity;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
public class ProtectionLayer {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long protectionLayerId;
	private String protectionLayerName;

	
	@OneToMany(mappedBy = "protectionLayer",fetch= FetchType.LAZY)
	@JsonManagedReference
	private List<AgriPvProtectionHeight> agriPvProtectionHeight;

	@Builder.Default
	private Boolean isActive = true;
	
//  @NotNull(message = "polysheets.not.null")
	@Column(name = "polysheets")
	private String polysheets;

//  @NotNull(message = "link_to_texture.not.null")
	@Column(name = "link_to_texture")
	private String linkToTexture;

//  @NotNull(message = "diffusion_fraction.not.null")
	@Column(name = "diffusion_fraction")
	private Double diffusionFraction;

//  @NotNull(message = "transmission_percentage.not.null")
	@Column(name = "transmission_percentage")
	private Double transmissionPercentage;

//  @NotNull(message = "void_percentage.not.null")
	@Column(name = "void_percentage")
	private Double voidPercentage;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "optical_property_id")
//	@JsonBackReference
	@JsonManagedReference
	private OpticalProperty opticalProperty;

	@Column(name = "f1")
	private Double f1;

	@Column(name = "f2")
	private Double f2;

	@Column(name = "f3")
	private Double f3;

	@Column(name = "f4")
	private Double f4;

	@Builder.Default
	private Boolean hide = true;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

}
