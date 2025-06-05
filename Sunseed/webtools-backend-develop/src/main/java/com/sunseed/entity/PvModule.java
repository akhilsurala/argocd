package com.sunseed.entity;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor

@ToString
@Builder

public class PvModule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String moduleType; // Kharif, num inverters, zaid

	private Double length;
	private Double width;
	
	@Column(name = "manufacturer_name")
    private String manufacturerName;

    @Column(name = "module_name")
    private String moduleName;

    @Column(name = "shortcode")
    private String shortcode;

    @Column(name = "module_tech")
    private String moduleTech;

    @Column(name = "link_to_data_sheet")
    private String linkToDataSheet;

	@Column(name = "num_cell_x")
	private Integer numCellX;

	@Column(name = "num_cell_y")
	private Integer numCellY;

	@Column(name = "longer_side")
	private Integer longerSide;

	@Column(name = "shorter_side")
	private Integer shorterSide;

	@Column(name = "thickness")
	private Integer thickness;

	@Column(name = "void_ratio")
	private Float voidRatio;

	@Column(name = "x_cell")
	private Float xCell;

	@Column(name = "y_cell")
	private Float yCell;

	@Column(name = "x_cell_gap")
	private Float xCellGap;

	@Column(name = "y_cell_gap")
	private Float yCellGap;

	@Column(name = "v_map")
	private Float vMap;

	@Column(name = "i_map")
	private Float iMap;

	@Column(name = "idc0")
	private Float idc0;

	@Column(name = "pdc0")
	private Float pdc0;

	@Column(name = "n_effective")
	private Float nEffective;

	@Column(name = "v_oc")
	private Float vOc;

	@Column(name = "i_sc")
	private Float iSc;

	@Column(name = "alpha_sc")
	private Float alphaSc;

	@Column(name = "beta_voc")
	private Float betaVoc;

	@Column(name = "gamma_pdc")
	private Float gammaPdc;

	@Column(name = "tem_ref")
	private Float temRef;

	@Column(name = "rad_sun")
	private Float radSun;

	@OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "front_optical_property_id")
    private OpticalProperty frontOpticalProperty;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "back_optical_property_id")
    private OpticalProperty backOpticalProperty;

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

	@Builder.Default
	private Boolean isActive = true;

	@Builder.Default
	private Boolean hide = true;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	private Instant updatedAt;

//	@OneToMany
//	private PvParameter pvParameter;
}
