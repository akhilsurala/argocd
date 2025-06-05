//package com.sunseed.simtool.service;
//
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.LocalTime;
//
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import com.sunseed.simtool.serviceimpl.EPWServiceImpl;
//import com.sunseed.simtool.serviceimpl.EPWServiceImpl.WeatherData;
//
//import lombok.extern.slf4j.Slf4j;
//
//@ExtendWith(MockitoExtension.class)
//@Slf4j
//public class EPWServiceImplTests {
//
//	@InjectMocks
//	private EPWServiceImpl epwServiceImpl;
//	
//	@Test
//	public void print_getWeatherInfoForDateTime()
//	{
//		LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0));
//		
//		while(startDate.isBefore(LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(0, 0))))
//		{
//			WeatherData weatherData = epwServiceImpl.getWeatherInfoForDateTime(startDate);
//			
//			log.info(startDate.toString() + " : " + weatherData.toString());
//			
//			startDate = startDate.plusHours(1l);
//		}
//	}
//}
