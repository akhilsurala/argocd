package com.sunseed.simtool.helper;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.simtool.constant.SimulationType;
import com.sunseed.simtool.entity.AgriBlockSimulationDetails;
import com.sunseed.simtool.entity.SimulationBlock;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Component
@RequiredArgsConstructor
public class SimulationBlockWiseHelper {

	private final ObjectMapper objectMapper;

	public List<SimulationBlock> getSimulationBlocksData(Map<String, Object> runPayload, SimulationType simulationType,
			Integer simulationDurationInDays, String simulationRunPolicy) {
		JsonNode payload = objectMapper.valueToTree(runPayload);

		Integer simulationRunPolicyInDays = getSimulationRunPolicyInDays(simulationRunPolicy);

		LocalDate simulationStartDate = validateDurationAndGetSimulationStartDate(payload, simulationType,
				simulationDurationInDays);
		LocalDate simulationEndDate = simulationStartDate.plusDays(simulationDurationInDays - 1);

		// for only_pv case
		if (simulationType.equals(SimulationType.ONLY_PV)) {
			List<SimulationBlock> simulationBlockData = getOnlyPvSimulationBlockData(simulationStartDate,
					simulationEndDate, simulationDurationInDays, simulationRunPolicyInDays);
			return simulationBlockData;
		}

		else if (simulationType.equals(SimulationType.ONLY_AGRI)) {
			List<SimulationBlock> simulationBlockData = getOnlyAgriSimulationBlockData(simulationStartDate,
					simulationEndDate, payload, simulationDurationInDays, simulationRunPolicyInDays);
			return simulationBlockData;
		}

		else {
			List<SimulationBlock> simulationBlockData = getAPVSimulationBlockData(simulationStartDate,
					simulationEndDate, payload, simulationDurationInDays, simulationRunPolicyInDays);
			return simulationBlockData;
		}
	}

	private List<SimulationBlock> getAPVSimulationBlockData(LocalDate simulationStartDate, LocalDate simulationEndDate,
			JsonNode payload, Integer simulationDurationInDays, Integer simulationRunPolicyInDays) {
		if (payload.has("cropParameters") && payload.get("cropParameters").has("cycles")) {

			List<SimulationBlock> simulationBlockData = new ArrayList<>();
			List<BlockDateRange> blockDateRanges = getBlockIndexWithStartDateAndEndDate(simulationStartDate,
					simulationEndDate, simulationDurationInDays, simulationRunPolicyInDays);

			JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
			Map<Integer, List<SimulationBlock>> blockDataForCycles = getBlockDataForCycles(cyclesNode,
					simulationDurationInDays, simulationRunPolicyInDays);

			Set<Integer> addedIndexFromCycle = new HashSet<>();

			for (BlockDateRange blockDateRange : blockDateRanges) {
				if (!blockDataForCycles.containsKey(blockDateRange.getBlockIndex())) {
					SimulationBlock simBlock = getSimulationBlockForPvBlock(simulationStartDate, blockDateRange,
							simulationDurationInDays, simulationRunPolicyInDays);
					simulationBlockData.add(simBlock);
				} else {
					if (addedIndexFromCycle.contains(blockDateRange.getBlockIndex()))
						continue;
					List<SimulationBlock> simBlocks = blockDataForCycles.get(blockDateRange.getBlockIndex());
					simBlocks.forEach(t -> {
						t.setCycleName(t.getCycleName()==null?"APV_CYCLE":t.getCycleName());
						t.setBlockSimulationType(SimulationType.APV);
						int runningDays = (int) (ChronoUnit.DAYS.between(t.getBlockStartDate(), t.getBlockEndDate())
								+ 1);
						t.setRunningDaysInBlockForPv(runningDays);
					});
					simulationBlockData.addAll(simBlocks);
					addedIndexFromCycle.add(blockDateRange.getBlockIndex());
				}
			}
			return simulationBlockData;
		}
		throw new RuntimeException("Cycles not found in APV case");
	}

	private List<SimulationBlock> getOnlyAgriSimulationBlockData(LocalDate simulationStartDate,
			LocalDate simulationEndDate, JsonNode payload, Integer simulationDurationInDays,
			Integer simulationRunPolicyInDays) {
		if (payload.has("cropParameters") && payload.get("cropParameters").has("cycles")) {

			List<SimulationBlock> simulationBlockData = new ArrayList<>();
			List<BlockDateRange> blockDateRanges = getBlockIndexWithStartDateAndEndDate(simulationStartDate,
					simulationEndDate, simulationDurationInDays, simulationRunPolicyInDays);

			JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
			Map<Integer, List<SimulationBlock>> blockDataForCycles = getBlockDataForCycles(cyclesNode,
					simulationDurationInDays, simulationRunPolicyInDays);
			Set<Integer> addedIndexFromCycle = new HashSet<>();

			for (BlockDateRange blockDateRange : blockDateRanges) {
				if (!blockDataForCycles.containsKey(blockDateRange.getBlockIndex()))
					continue;
				if (addedIndexFromCycle.contains(blockDateRange.getBlockIndex()))
					continue;
				List<SimulationBlock> simulationBlocks = blockDataForCycles.get(blockDateRange.getBlockIndex());
				simulationBlocks.forEach(t -> {
					t.setCycleName(t.getCycleName()==null?"ONLY_AGRI_CYCLE":t.getCycleName());
					t.setBlockSimulationType(SimulationType.ONLY_AGRI);
				});
				simulationBlockData.addAll(simulationBlocks);
				addedIndexFromCycle.add(blockDateRange.getBlockIndex());
			}
			return simulationBlockData;
		}
		throw new RuntimeException("Cycles not found for Only Agri case");
	}

	private Map<Integer, List<SimulationBlock>> getBlockDataForCycles(JsonNode cyclesNode,
			Integer simulationDurationInDays, Integer simulationRunPolicyInDays) {

		if (!cyclesNode.isNull() && cyclesNode.isArray()) {

			Map<Integer, List<SimulationBlock>> simulationBlockDataForCycles = new HashMap<>();
			for (JsonNode cycle : cyclesNode) {
				LocalDate cycleStartDate = LocalDate.parse(cycle.get("cycleStartDate").asText());
				Integer cycleDurationInDays = cycle.get("duration").asInt();
				LocalDate cycleEndDate = cycleStartDate.plusDays(cycleDurationInDays - 1);

				List<BlockDateRange> blockDateRanges = getBlockIndexWithStartDateAndEndDate(cycleStartDate,
						cycleEndDate, simulationDurationInDays, simulationRunPolicyInDays);

				for (int start = 0; start < blockDateRanges.size(); start++) {
					SimulationBlock simBlock = new SimulationBlock();
					simBlock.setCycleStartDate(cycleStartDate);
					simBlock.setCycleEndDate(cycleEndDate);
					simBlock.setCycleName(cycle.get("cycleName").asText());
					simBlock.setCycleDurationInDays(cycleDurationInDays);
					simBlock.setBlockIndex(blockDateRanges.get(start).getBlockIndex());

					LocalDate blockStartDate = blockDateRanges.get(start).getBlockStartDate();
					LocalDate blockEndDate = blockDateRanges.get(start).getBlockEndDate();

					simBlock.setBlockStartDate(blockStartDate);
					simBlock.setBlockEndDate(blockEndDate);

					LocalDate simulationBlockDate = null;
					if (start == 0)
						simBlock.setBlockSimulationDate(cycleStartDate);

					boolean cropFound = false;
					simBlock.setAgriBlockSimulationDetails(new ArrayList<>());

					JsonNode beds = cycle.get("cycleBedDetails");
					for (JsonNode bed : beds) {
						String bedName = bed.get("bedName").asText();
						Integer bedIndex = bedName == null ? 0 : Integer.parseInt(bedName.trim().substring(4)); // assuming
																												// Bed 1
																												// this
																												// will
																												// be
																												// format
						JsonNode crops = bed.get("cropDetails");
						for (JsonNode crop : crops) {
							LocalDate cropStartDate = cycleStartDate;
							Integer cropDurationInDays = crop.get("duration").asInt();
							LocalDate cropEndDate = cycleStartDate.plusDays(cropDurationInDays - 1);
							if (cropFound(blockStartDate, blockEndDate, cropStartDate, cropEndDate)) {
								cropFound = true;
								AgriBlockSimulationDetails agriBlockDetails = new AgriBlockSimulationDetails();
								agriBlockDetails.setBedName(bedName);
								agriBlockDetails.setBedIndex(bedIndex);
								agriBlockDetails.setCropName(crop.get("cropLabel").asText());
								agriBlockDetails.setCropStartDate(cropStartDate);
								agriBlockDetails.setCropEndDate(cropEndDate);
								agriBlockDetails.setDuration(cropDurationInDays);
								agriBlockDetails.setMinStage(crop.get("minStage").asInt());
								agriBlockDetails.setMaxStage(crop.get("maxStage").asInt());

								Integer runningDaysInBlock = 0;
								Integer cropAge = 0;

								// if crop ends in the block
								if (cropEndsInBlock(blockStartDate, blockEndDate, cropStartDate, cropEndDate)) {
									runningDaysInBlock = (int) (ChronoUnit.DAYS.between(blockStartDate, cropEndDate)
											+ 1);
									cropAge = (int) (ChronoUnit.DAYS.between(cropStartDate, cropEndDate) + 1);

									// for setting up the simulation block date to the date of earliest maturing
									// crop
									if (start != 0) {
										if (simulationBlockDate == null || (simulationBlockDate != null
												&& cropEndDate.isBefore(simulationBlockDate)))
											simulationBlockDate = cropEndDate;
									}
								}

								// if crop is found but not ends in the block
								else {
									if (start != 0)
										runningDaysInBlock = (int) (ChronoUnit.DAYS.between(blockStartDate,
												blockEndDate) + 1);
									else
										runningDaysInBlock = (int) (ChronoUnit.DAYS.between(cropStartDate, blockEndDate)
												+ 1);
									cropAge = (int) (ChronoUnit.DAYS.between(cropStartDate, blockEndDate) + 1);
								}

								agriBlockDetails.setRunningDaysInBlock(runningDaysInBlock);
								agriBlockDetails.setCropAge(cropAge);
								agriBlockDetails.setSimulationBlock(simBlock);
								simBlock.getAgriBlockSimulationDetails().add(agriBlockDetails);
							}
						}
					}

					// add simulation block date
					if (start != 0) {

						// crop is found running and also ends in the block
						if (cropFound && simulationBlockDate != null) {
							simBlock.setBlockSimulationDate(simulationBlockDate);
						}

						// crop is found running but no crop ended in the block
						if (cropFound && simulationBlockDate == null) {
							LocalDate blockSimulationDate = findMidIndexAndGetBlockSimulationDate(blockStartDate,
									blockEndDate);
							simBlock.setBlockSimulationDate(blockSimulationDate);
						}
					}

					simulationBlockDataForCycles.computeIfAbsent(simBlock.getBlockIndex(), k -> new ArrayList<>())
							.add(simBlock);
				}
			}
			return simulationBlockDataForCycles;
		}
		throw new RuntimeException("Cycles not found");
	}

	private boolean cropEndsInBlock(LocalDate blockStartDate, LocalDate blockEndDate, LocalDate cropStartDate,
			LocalDate cropEndDate) {
		// TODO Auto-generated method stub
		return cropEndDate.isAfter(blockStartDate.minusDays(1)) && cropEndDate.isBefore(blockEndDate.plusDays(1));
	}

	private boolean cropFound(LocalDate blockStartDate, LocalDate blockEndDate, LocalDate cropStartDate,
			LocalDate cropEndDate) {

		// Check if crop start date is before or on block end date, and crop end date is
		// after or on block start date
		return (cropStartDate.isBefore(blockEndDate) || cropStartDate.isEqual(blockEndDate))
				&& (cropEndDate.isAfter(blockStartDate) || cropEndDate.isEqual(blockStartDate));
	}

	private List<SimulationBlock> getOnlyPvSimulationBlockData(LocalDate simulationStartDate,
			LocalDate simulationEndDate, Integer simulationDurationInDays, Integer simulationRunPolicyInDays) {

		List<SimulationBlock> simulationBlockData = new ArrayList<>();

		List<BlockDateRange> blockDateRanges = getBlockIndexWithStartDateAndEndDate(simulationStartDate,
				simulationEndDate, simulationDurationInDays, simulationRunPolicyInDays);

		for (BlockDateRange blockDateRange : blockDateRanges) {
			SimulationBlock simBlock = getSimulationBlockForPvBlock(simulationStartDate, blockDateRange,
					simulationDurationInDays, simulationRunPolicyInDays);
			simulationBlockData.add(simBlock);
		}
		return simulationBlockData;
	}

	private SimulationBlock getSimulationBlockForPvBlock(LocalDate simulationStartDate, BlockDateRange blockDateRange,
			Integer simulationDurationInDays, Integer simulationRunPolicyInDays) {
		SimulationBlock simBlock = new SimulationBlock();
		simBlock.setBlockIndex(blockDateRange.getBlockIndex());
		simBlock.setAgriBlockSimulationDetails(null);
		simBlock.setCycleStartDate(simulationStartDate);
		simBlock.setCycleEndDate(simulationStartDate.plusDays(simulationDurationInDays - 1));
		simBlock.setCycleName("ONLY_PV_CYCLE");
		simBlock.setBlockSimulationType(SimulationType.ONLY_PV);
		simBlock.setCycleDurationInDays(simulationDurationInDays);
		int runningDays = (int) (blockDateRange.getBlockEndDate().toEpochDay()
				- blockDateRange.getBlockStartDate().toEpochDay() + 1);
		simBlock.setRunningDaysInBlockForPv(runningDays);
		simBlock.setBlockStartDate(blockDateRange.getBlockStartDate());
		simBlock.setBlockEndDate(blockDateRange.getBlockEndDate());
		LocalDate blockSimulationDate = findMidIndexAndGetBlockSimulationDate(blockDateRange.getBlockStartDate(),
				blockDateRange.getBlockEndDate());
		simBlock.setBlockSimulationDate(blockSimulationDate);
		return simBlock;
	}

	private LocalDate findMidIndexAndGetBlockSimulationDate(LocalDate blockStartDate, LocalDate blockEndDate) {
		long totalDays = blockEndDate.toEpochDay() - blockStartDate.toEpochDay() + 1;
		long middleIndex = totalDays / 2;
		return blockStartDate.plusDays(middleIndex - 1);
	}

	public Integer getSimulationRunPolicyInDays(String simulationRunPolicy) {
		Integer simulationRunPolicyInDays = 0;
		switch (simulationRunPolicy) {
		case "DAILY":
			simulationRunPolicyInDays = 1;
			break;
		case "WEEKLY":
			simulationRunPolicyInDays = 7;
			break;
		case "BIWEEKLY":
			simulationRunPolicyInDays = 14;
			break;
		case "MONTHLY":
			simulationRunPolicyInDays = 30;
			break;
		case "45DAYS":
			simulationRunPolicyInDays = 45;
			break;
		default:
			simulationRunPolicyInDays = 14;
			break;
		}
		return simulationRunPolicyInDays;
	}

	public List<BlockDateRange> getBlockIndexWithStartDateAndEndDate(LocalDate startDate, LocalDate endDate,
			Integer simulationDurationInDays, Integer simulationRunPolicy) {

		List<BlockDateRange> blockDateRanges = new ArrayList<>();
		int maxNumberOfBlocks = getMaxNumberOfBlocks(simulationDurationInDays, simulationRunPolicy);
		while (startDate.isBefore(endDate) || startDate.isEqual(endDate)) {
			Integer blockIndex = getBlockIndex(startDate, simulationDurationInDays, simulationRunPolicy);
			LocalDate blockStartDate = LocalDate.of(startDate.getYear(), 1, 1)
					.plusDays((blockIndex - 1) * simulationRunPolicy);
			LocalDate blockEndDate = null;
			if (blockIndex == maxNumberOfBlocks) {
				blockEndDate = LocalDate.of(startDate.getYear(), 12, 31);
			} else if (blockIndex < maxNumberOfBlocks) {
				blockEndDate = blockStartDate.plusDays(simulationRunPolicy - 1);
			}
			blockDateRanges.add(new BlockDateRange(blockIndex, blockStartDate, blockEndDate));
			startDate = blockEndDate.plusDays(1);
		}
		blockDateRanges.sort(Comparator.comparing(BlockDateRange::getBlockStartDate));
		return blockDateRanges;
	}

	public Integer getBlockIndex(LocalDate date, Integer simulationDurationInDays, Integer simulationRunPolicy) {

		if (date == null || simulationDurationInDays == null || simulationRunPolicy == null
				|| simulationRunPolicy <= 0) {
			return null;
		}
		int maxNumberOfBlocks = getMaxNumberOfBlocks(simulationDurationInDays, simulationRunPolicy);
		LocalDate yearStartDate = LocalDate.of(2023, 1, 1); // keeping non leap year for consistency
		LocalDate reflectiveDate = LocalDate.of(2023, date.getMonth(), date.getDayOfMonth());
		long daysSinceStart = ChronoUnit.DAYS.between(yearStartDate, reflectiveDate) + 1;
		
		// if daysSinceStart are less than simulationRunPolicy simply return 1
		if(daysSinceStart<simulationRunPolicy)
			return 1;
		
		// else calculate blockIndex
		double blockIndex = (daysSinceStart+0.0) / simulationRunPolicy;
		if (blockIndex > maxNumberOfBlocks)
			return maxNumberOfBlocks;
		return (int) Math.ceil(blockIndex);
	}

	public int getMaxNumberOfBlocks(Integer simulationDurationInDays, Integer simulationRunPolicy) {
		return simulationDurationInDays / simulationRunPolicy;
	}

	private LocalDate validateDurationAndGetSimulationStartDate(JsonNode payload, SimulationType simulationType,
			Integer simulationRunDurationInDays) {
		if (simulationType.equals(SimulationType.ONLY_PV))
			return LocalDate.of(LocalDate.now().getYear(), 1, 1);

		List<Cycle> cycleList = getCycleList(payload);
		cycleList.sort(Comparator.comparing(Cycle::getCycleStartDate));
		LocalDate startDate = cycleList.get(0).getCycleStartDate();
		LocalDate endDate = cycleList.get(cycleList.size() - 1).getCycleEndDate();
		LocalDate expectedEndDate = startDate.plusDays(simulationRunDurationInDays - 1);
		if (endDate.isAfter(expectedEndDate))
			throw new RuntimeException("Cycles Duration exceeded");
		return startDate;
	}

	private List<Cycle> getCycleList(JsonNode payload) {
		if (payload.has("cropParameters") && payload.get("cropParameters").has("cycles")) {
			JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
			if (!cyclesNode.isNull() && cyclesNode.isArray()) {
				List<Cycle> cycleList = new ArrayList<>();
				for (JsonNode cycle : cyclesNode) {
					LocalDate cycleStartDate = LocalDate.parse(cycle.get("cycleStartDate").asText());
					Integer cycleDurationInDays = cycle.get("duration").asInt();
					LocalDate cycleEndDate = cycleStartDate.plusDays(cycleDurationInDays - 1);
					Cycle newCycle = new Cycle(cycleStartDate, cycleEndDate);
					cycleList.add(newCycle);
				}
				return cycleList;
			}

		}
		throw new RuntimeException("Cycles not found");
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	class BlockDateRange {
		private final int blockIndex;
		private final LocalDate blockStartDate;
		private final LocalDate blockEndDate;
	}

	@Getter
	@Setter
	@RequiredArgsConstructor
	class Cycle {
		private final LocalDate cycleStartDate;
		private final LocalDate cycleEndDate;
	}
}