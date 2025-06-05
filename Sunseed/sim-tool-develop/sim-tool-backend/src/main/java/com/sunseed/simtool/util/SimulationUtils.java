package com.sunseed.simtool.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.sunseed.simtool.entity.SimulationTask;

public class SimulationUtils {

	public static List<LocalDate> nextSimulationTaskDate(String simulationRunPolicy, LocalDate startDate, LocalDate endDate,
			JsonNode cycle)
	{
		List<LocalDate> simulationDates = new ArrayList<>();
		
		LocalDate date = startDate;
//		simulationDates.add(date);
		while(date.isBefore(endDate)) {
			
			simulationDates.add(date);
			
			if(simulationRunPolicy == null)
				date = date.plusWeeks(2l);
			else if(simulationRunPolicy.equalsIgnoreCase("DAILY"))
				date = date.plusDays(1l);
			else if(simulationRunPolicy.equalsIgnoreCase("WEEKLY"))
				date = date.plusWeeks(1l);
			else if(simulationRunPolicy.equalsIgnoreCase("BIWEEKLY"))
				date = date.plusWeeks(2l);
			else if(simulationRunPolicy.equalsIgnoreCase("MONTHLY"))
				date = date.plusDays(30l);
			else if(simulationRunPolicy.equalsIgnoreCase("45DAYS"))
				date = date.plusDays(45l);
		}
		
		if(cycle != null) {
			JsonNode cycleBedDetails = cycle.get("cycleBedDetails");
			for(JsonNode cycleBed : cycleBedDetails)
			{
				JsonNode cropDetails = cycleBed.get("cropDetails");
				for(JsonNode crop : cropDetails)
				{
					int duration = crop.get("duration").asInt(); //in days
					int intervalInDays = 0;
					
					if(simulationRunPolicy.equalsIgnoreCase("DAILY"))
						intervalInDays = 1;
					else if(simulationRunPolicy.equalsIgnoreCase("WEEKLY"))
						intervalInDays = 7;
					else if(simulationRunPolicy.equalsIgnoreCase("BIWEEKLY"))
						intervalInDays = 14;
					else if(simulationRunPolicy.equalsIgnoreCase("MONTHLY"))
						intervalInDays = 30;
					else if(simulationRunPolicy.equalsIgnoreCase("45DAYS"))
						intervalInDays = 45;
					
					if((duration-1) % intervalInDays != 0)
					{
						simulationDates.add(startDate.plusDays(duration));
					}
				}
			}
		}
		
		return simulationDates.stream().sorted().distinct().collect(Collectors.toList());
	}
	
	public static LocalDateTime nextSimulationTaskTime(Integer simulationDailyRunHours, LocalDateTime dateTime)
	{
		if(simulationDailyRunHours == null || simulationDailyRunHours == 0)
			return dateTime.plusHours(1l);
		
		return dateTime.plusHours(simulationDailyRunHours);
	}
	
	public static JsonNode getCycleNode(JsonNode payload, SimulationTask simulationTask) {
		JsonNode cyclesNode = payload.get("cropParameters").get("cycles");
		JsonNode cycle = null;
		if (cyclesNode.isArray()) {
			for (JsonNode node : cyclesNode) {
				LocalDate startDate = LocalDate.parse(node.get("cycleStartDate").asText());
				Integer duration = node.get("duration").asInt();
				LocalDate endDate = startDate.plusDays(duration-1);
				LocalDate simulationTaskDate = simulationTask.getDate().toLocalDate();

				if (!simulationTaskDate.isBefore(startDate)
						&& !simulationTaskDate.isAfter(endDate)) {
					cycle = node;
					break;
				}
			}
		}
		return cycle;
	}
	
	public static String convertToTimeZoneString(double offset) {
        int hours = (int) offset;
        int minutes = (int) ((offset - hours) * 60);
        
        String sign = offset >= 0 ? "+" : "-";
        hours = Math.abs(hours);
        minutes = Math.abs(minutes);
        
        return String.format("%s%02d:%02d", sign, hours, minutes);
    }
}
