package com.sunseed.simtool.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.Getter;
import lombok.Setter;

public class PayloadExtractor {

	public static Map<String, CycleData> extractCycleDataFromPayload(JsonNode runPayload) {
		Map<String, CycleData> result = new HashMap<>();

		JsonNode cropParameters = runPayload.get("cropParameters");
		if (cropParameters == null || !cropParameters.has("cycles") || !cropParameters.get("cycles").isArray())
			return result;

		// it is same for all cycles
		int bedCount = cropParameters.has("bedParameter") ? cropParameters.get("bedParameter").get("noOfBeds").asInt()
				: 1;

		for (JsonNode cycleNode : cropParameters.get("cycles")) {
			String cycleStartDate = cycleNode.get("cycleStartDate").asText();

			// Build CropValues map
			Map<String, Map<String, CropValues>> bedCropValues = new HashMap<>();
			JsonNode cropBedDetails = cycleNode.get("cycleBedDetails");
			for (JsonNode bedDetail : cropBedDetails) {
				String bedName = bedDetail.get("bedName").asText();
				JsonNode cropDetails = bedDetail.get("cropDetails");
				if (cropDetails != null && cropDetails.isArray()) {
					for (JsonNode cropDetail : cropDetails) {
						String cropName = cropDetail.get("cropLabel").asText();
						double o1 = cropDetail.has("o1") ? cropDetail.get("o1").asDouble() : 0.0;
						double s1 = cropDetail.has("s1") ? cropDetail.get("s1").asDouble() : 0.0;
						double f5 = cropDetail.has("f5") ? cropDetail.get("f5").asDouble() : 0.0;

						bedCropValues.computeIfAbsent(bedName, k -> new HashMap<>()).put(cropName,
								new CropValues(cropName, o1, s1, f5));
					}
				}
			}

			// Read interBedPattern and bedCount
			List<String> interBedPattern = new ArrayList<>();
			JsonNode patternNode = cycleNode.get("interBedPattern");
			if (patternNode != null && patternNode.isArray()) {
				for (JsonNode p : patternNode) {
					interBedPattern.add(p.asText());
				}
			}

			// Store in CycleData
			CycleData cycleData = new CycleData(cycleStartDate, bedCropValues, interBedPattern, bedCount);
			result.put(cycleStartDate, cycleData);
		}

		return result;
	}

	@Getter
	@Setter
	public static class CycleData {

		public String cycleStartDate;
		public Map<String, Map<String, CropValues>> cropValues;
		public List<String> interBedPattern;
		public int bedCount;

		public CycleData(String cycleStartDate, Map<String, Map<String, CropValues>> cropValues,
				List<String> interBedPattern, int bedCount) {
			this.cycleStartDate = cycleStartDate;
			this.cropValues = cropValues;
			this.interBedPattern = interBedPattern;
			this.bedCount = bedCount;
		}
	}

	@Getter
	@Setter
	public static class CropValues {
		public String cropName;
		public double o1;
		public double s1;
		public double f5;

		public CropValues(String cropName, double o1, double s1, double f5) {
			this.cropName = cropName;
			this.o1 = o1;
			this.s1 = s1;
			this.f5 = f5;
		}
	}
}
