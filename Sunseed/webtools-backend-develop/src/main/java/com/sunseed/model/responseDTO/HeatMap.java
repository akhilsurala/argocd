package com.sunseed.model.responseDTO;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Date;

import com.sunseed.model.requestDTO.Simulation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HeatMap {
	
	private LocalDate date;
	private LocalTime time;
	private Float temperature;

}
