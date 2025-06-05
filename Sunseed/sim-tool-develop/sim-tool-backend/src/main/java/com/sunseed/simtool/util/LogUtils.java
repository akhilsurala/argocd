package com.sunseed.simtool.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LogUtils {

	public static String extractNumericalValue(String logs, String key) {
		Pattern linePattern = Pattern.compile(".*" + key + ".*");
		Matcher lineMatcher = linePattern.matcher(logs);

		String keyLine = null;
		while (lineMatcher.find()) {
			keyLine = lineMatcher.group();
			break;
		}

		if (keyLine != null) {
			Pattern valuePattern = Pattern.compile(key + "\\s*:\\s*([+-]?\\d+(\\.\\d+)?)");
			Matcher valueMatcher = valuePattern.matcher(keyLine);

			if (valueMatcher.find()) {
				return valueMatcher.group(1);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static String extractUrlValue(String logs, String key) {
		Pattern linePattern = Pattern.compile(".*" + key + ".*");
		Matcher lineMatcher = linePattern.matcher(logs);

		String keyLine = null;
		while (lineMatcher.find()) {
			keyLine = lineMatcher.group();
			break;
		}

		if (keyLine != null) {
			Pattern valuePattern = Pattern.compile(key + "\\s*:\\s*(https?://\\S+)");
			Matcher valueMatcher = valuePattern.matcher(keyLine);

			if (valueMatcher.find()) {
				return valueMatcher.group(1);
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static String extractStringValue(String logs, String key) {
		// Pattern to match the line containing the key
		Pattern linePattern = Pattern.compile(".*" + key + ".*");
		Matcher lineMatcher = linePattern.matcher(logs);

		String keyLine = null;
		// Find the line containing the key
		while (lineMatcher.find()) {
			keyLine = lineMatcher.group();
			break;
		}

		if (keyLine != null) {
			// Pattern to extract the string value after the key
			// The value is expected to be any sequence of characters after the colon,
			// excluding newlines
			Pattern valuePattern = Pattern.compile(key + "\\s*:\\s*\"?([^\"]+)\"?");
			Matcher valueMatcher = valuePattern.matcher(keyLine);

			if (valueMatcher.find()) {
				return valueMatcher.group(1).trim(); // Extract the string value and trim any extra spaces
			} else {
				return null; // If no value found after the key
			}
		} else {
			return null; // If the key is not found in the logs
		}
	}

	public static Map<Integer, Map<String, Double>> extractCarbonAssimilationValues(String logs) {
		Map<Integer, Map<String, Double>> result = new HashMap<>();
		String regex = "carbon_assimilation_(\\w+)_(\\d+)\\s*:\\s*(-?\\d+\\.\\d+)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(logs);

		while (matcher.find()) {
			String crop = matcher.group(1);
			int bedIndex = Integer.parseInt(matcher.group(2));
			double value = Double.parseDouble(matcher.group(3));
			Map<String, Double> cropMap = result.getOrDefault(bedIndex, new HashMap<>());
			cropMap.put(crop, value);

			// Put the map back in the result
			result.put(bedIndex, cropMap);
		}

		return result.isEmpty() ? null : result;
	}

	public static Map<Integer, Map<String, Map<String, Map<String, Double>>>> extractAgriSimulationValues(String logs) {
		// Map<bedIndex, Map<cropName, Map<leafType, Map<outputName, value>>>>
		Map<Integer, Map<String, Map<String, Map<String, Double>>>> result = new HashMap<>();

		// Regex to match multiple output parameters (carbon_assimilation, temperature,
		// etc.)
		String regex = "(carbon_assimilation|temperature|radiation|saturation|penetration|latent_flux|leaves_area|crop_count|saturation_extent)_(sunlit|sunshaded)_(\\w+)_(\\d+)\\s*:\\s*(-?\\d+(?:\\.\\d+)?)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(logs);

		while (matcher.find()) {
			String outputParameter = matcher.group(1); // carbon_assimilation, temperature, etc.
			String leafType = matcher.group(2); // sunlit or sunshaded
			String cropName = matcher.group(3); // crop name
			int bedIndex = Integer.parseInt(matcher.group(4)); // bed index
			double value = Double.parseDouble(matcher.group(5)); // value (e.g., 3.45)

			// Nested maps to hold data for each bed and crop
			result.computeIfAbsent(bedIndex, k -> new HashMap<>()).computeIfAbsent(cropName, k -> new HashMap<>())
					.computeIfAbsent(leafType, k -> new HashMap<>()).put(outputParameter, value);
		}

		// Validate if all crops in all beds have 'carbon_assimilation' in either
		// 'sunlit' or 'sunshaded'
		boolean allCropsValid = result.values().stream()
				.allMatch(cropsMap -> cropsMap.values().stream()
						.allMatch(leafTypes -> (leafTypes.containsKey("sunlit")
								&& leafTypes.get("sunlit").containsKey("carbon_assimilation"))
								|| (leafTypes.containsKey("sunshaded")
										&& leafTypes.get("sunshaded").containsKey("carbon_assimilation"))));

		// If any crop is missing carbon_assimilation in both sunlit and sunshaded,
		// return null
		if (!allCropsValid) {
			return null;
		}

		return result.isEmpty() ? null : result;
	}

	public static void saveSimulationResult(Map<String, Object> result) {
		StringBuilder builder = new StringBuilder();
		for (Map.Entry<String, Object> entry : result.entrySet()) {
			builder.append(entry.getKey() + " : " + entry.getValue() + "\n");
		}
		log.debug("\n" + builder.toString() + "\n--------------------------------------------");
	}
}
