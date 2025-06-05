package com.sunseed.simtool.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeoJsonId implements Serializable{

	private static final long serialVersionUID = 1411314814437439093L;
	private Double longitude;
	private Double latitude;
}
