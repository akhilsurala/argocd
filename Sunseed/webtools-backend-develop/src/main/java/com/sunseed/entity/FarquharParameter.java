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
@Table(name = "farquhar_parameter")
public class FarquharParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "vc_max")
	private Double vcMax;

	@NotNull
	@Column(name = "j_max")
	private Double jMax;

	@NotNull
	@Column(name = "cj_max")
	private Double cjMax;

	@NotNull
	@Column(name = "ha_j_max")
	private Double haJMax;
	
	@NotNull
	@Column(name = "alpha")
	private Double alpha;
	
	@NotNull
	@Column(name = "rd_25")
	private Double rd25;

	@OneToOne(mappedBy = "farquharParameter", fetch = FetchType.LAZY)
    @JsonBackReference  // Prevent circular reference
    private Crop crop;
}
