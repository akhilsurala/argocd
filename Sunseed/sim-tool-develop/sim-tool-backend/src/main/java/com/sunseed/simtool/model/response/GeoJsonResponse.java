package com.sunseed.simtool.model.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GeoJsonResponse {

	private String type;
	private List<Feature> features;
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Feature {
		private String type;
		private Property properties;
		private Geometry geometry;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Property {
		private String title;
		private String epw;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class Geometry {
		private String type;
		private Double[] coordinates;
	}
}
