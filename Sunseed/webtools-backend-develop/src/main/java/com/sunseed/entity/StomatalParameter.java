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
@Table(name = "stomatal_parameter")
public class StomatalParameter {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotNull
	@Column(name = "em")
	private Double em;

	@NotNull
	@Column(name = "io")
	private Double io;

	@NotNull
	@Column(name = "k")
	private Double k;

	@NotNull
	@Column(name = "b")
	private Double b;

	@OneToOne(mappedBy = "stomatalParameter", fetch = FetchType.LAZY)
    @JsonBackReference  // Prevent circular reference
    private Crop crop;
}
