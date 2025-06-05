package com.sunseed.simtool.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sunseed.simtool.entity.SimulationTask;

@ExtendWith(MockitoExtension.class)
public class SimulationUtilsTest {
	
	@Test
	public void testClassInit()
	{
		assertThat(new SimulationUtils()).isNotNull();
	}

	@Test
	public void test_nextSimulationTaskDate()
	{
		assertThat(SimulationUtils.nextSimulationTaskDate("DAILY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 7), null), 
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 4),
						LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6)));
		assertThat(SimulationUtils.nextSimulationTaskDate("WEEKLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), null),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 8), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 22),
						LocalDate.of(2024, 4, 29)));
		assertThat(SimulationUtils.nextSimulationTaskDate("BIWEEKLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), null),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 29)));
		assertThat(SimulationUtils.nextSimulationTaskDate("MONTHLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 6, 5), null),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 31)));
		assertThat(SimulationUtils.nextSimulationTaskDate(null, LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), null),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 29)));
	}
	
	@Test
	public void test_nextSimulationTaskDate_withCycle()
	{
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		
		ObjectNode cycle = jsonNodeFactory.objectNode();
		ArrayNode cycleBedDetails = jsonNodeFactory.arrayNode();
		ObjectNode cropBed = jsonNodeFactory.objectNode();
		ArrayNode cropDetails = jsonNodeFactory.arrayNode();
		ObjectNode crop = jsonNodeFactory.objectNode();
		crop.put("duration", 50);
		cropDetails.add(crop);
		cropBed.set("cropDetails", cropDetails);
		cycleBedDetails.add(cropBed);
		cycle.set("cycleBedDetails", cycleBedDetails);
		
		assertThat(SimulationUtils.nextSimulationTaskDate("DAILY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 7), cycle), 
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 2), LocalDate.of(2024, 4, 3), LocalDate.of(2024, 4, 4),
						LocalDate.of(2024, 4, 5), LocalDate.of(2024, 4, 6)));
		
		assertThat(SimulationUtils.nextSimulationTaskDate("WEEKLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), cycle),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 8), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 22),
						LocalDate.of(2024, 4, 29)));
		
		assertThat(SimulationUtils.nextSimulationTaskDate("BIWEEKLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), cycle),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 4, 15), LocalDate.of(2024, 4, 29), LocalDate.of(2024, 5, 21)));
		
		assertThat(SimulationUtils.nextSimulationTaskDate("MONTHLY", LocalDate.of(2024, 4, 1), LocalDate.of(2024, 6, 5), cycle),
				contains(LocalDate.of(2024, 4, 1), LocalDate.of(2024, 5, 1), LocalDate.of(2024, 5, 21), LocalDate.of(2024, 5, 31)));
	}
	
	@Test
	public void test_nextSimulationTaskTime()
	{
		assertEquals(SimulationUtils.nextSimulationTaskTime(null, LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(6, 0))),
				LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(7, 0)));
		assertEquals(SimulationUtils.nextSimulationTaskTime(0, LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(6, 0))),
				LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(7, 0)));
		assertEquals(SimulationUtils.nextSimulationTaskTime(2, LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(6, 0))),
				LocalDateTime.of(LocalDate.of(2024, 4, 1), LocalTime.of(8, 0)));
	}
	
	@Test
	public void test_convertToTimeZoneString()
	{
		assertEquals(SimulationUtils.convertToTimeZoneString(8.0), "+08:00");
		assertEquals(SimulationUtils.convertToTimeZoneString(8.5), "+08:30");
		assertEquals(SimulationUtils.convertToTimeZoneString(-8.0), "-08:00");
		assertEquals(SimulationUtils.convertToTimeZoneString(-8.5), "-08:30");
	}
	
	@Test
	public void test_getCycleNode()
	{
		JsonNodeFactory jsonNodeFactory = JsonNodeFactory.instance;
		
		ObjectNode node = jsonNodeFactory.objectNode();
		ObjectNode cropParameters = jsonNodeFactory.objectNode();
		
		ArrayNode cycles = jsonNodeFactory.arrayNode();
		
		ObjectNode cycle1 = jsonNodeFactory.objectNode();
		ObjectNode cycle2 = jsonNodeFactory.objectNode();
		
		cycle1.put("cycleStartDate", "2024-01-01");
		cycle1.put("duration", "90");
		cycle2.put("cycleStartDate", "2024-04-01");
		cycle2.put("duration", "90");
		
		cycles.add(cycle1);
		cycles.add(cycle2);
		
		cropParameters.set("cycles", cycles);
		node.set("cropParameters", cropParameters);
		
		SimulationTask simulationTask = new SimulationTask();
		simulationTask.setDate(LocalDateTime.of(LocalDate.of(2024, 5, 1), LocalTime.MIDNIGHT));
		
		 assertEquals(SimulationUtils.getCycleNode(node, simulationTask), cycle2);
	}
}
