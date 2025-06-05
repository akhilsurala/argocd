package com.sunseed.simtool.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "simulation_ground_area", uniqueConstraints = { @UniqueConstraint(columnNames = { "simulation_block_id", "date" }) })
public class SimulationGroundArea extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonIgnore
	@ToString.Exclude
	@OneToOne
	@JoinColumn(name = "simulation_block_id")
	private SimulationBlock simulationBlock;
	
	@Column(name = "unit_x_length")
	private Double unitXLength;
	@Column(name = "unit_y_length")
	private Double unitYLength;
	@Column(name = "x_repetition")
	private Integer xRepetition;
	@Column(name = "y_repetition")
	private Integer yRepetition;
	@Column(name = "x_length")
	private Double xLength;
	@Column(name = "y_length")
	private Double yLength;
}
