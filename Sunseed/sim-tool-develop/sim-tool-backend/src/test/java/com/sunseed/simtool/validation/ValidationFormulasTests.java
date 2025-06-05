package com.sunseed.simtool.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ValidationFormulasTests {

	@InjectMocks
	private ValidationFormulas validationFormulas;
	
	@Test
	public void test_calculateMaxGapBetweenModules()
	{
		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 1, 3.0, 2.0, 45.0, false, 'P'), 397.0);
		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 1, 3.0, 2.0, 45.0, false, 'L'), 398.0);

		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 3, 3.0, 2.0, 45.0, false, 'P'), 30.855339, 0.000001);
		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 3, 3.0, 2.0, 45.0, false, 'L'), 32.355339, 0.000001);

		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 4, 3.0, 2.0, 45.0, true, 'P'), 6.672954, 0.000001);
		assertEquals(validationFormulas.calculateMaxGapBetweenModules(400.0, 50.0, 4, 3.0, 2.0, 45.0, true, 'L'), 7.815811, 0.000001);
	}
	
	@Test
	public void test_calculateModuleHeight()
	{
		assertEquals(validationFormulas.calculateModuleHeight(3, 5.0, 6.0, 30.0, 'P', 30.0), 18.75, 0.0000001);
		assertEquals(validationFormulas.calculateModuleHeight(3, 5.0, 6.0, 30.0, 'L', 30.0), 19.5, 0.0000001);
	}
	
	@Test
	public void test_calculatePitch()
	{
		assertEquals(validationFormulas.calculatePitch(3, 5.0, 6.0, 60.0, 'P', 30.0, false), 37.5, 0.000001);
		assertEquals(validationFormulas.calculatePitch(3, 5.0, 6.0, 60.0, 'L', 30.0, false), 39.0, 0.000001);
		
		assertEquals(validationFormulas.calculatePitch(3, 5.0, 6.0, 60.0, 'P', 30.0, true), 90.0, 0.000001);
		assertEquals(validationFormulas.calculatePitch(3, 5.0, 6.0, 60.0, 'L', 30.0, true), 93.0, 0.000001);
	}
	
	@Test
	public void test_calculateBedBottomWidth()
	{
		assertEquals(validationFormulas.calculateBedBottomWidth(45.0, 5.0, 2.0), 9.0);
		assertEquals(validationFormulas.calculateBedBottomWidth(30.0, 5.0, 2.0), 11.92, 0.01);
	}
	
	@Test
	public void test_calculateBedSpacing()
	{
		assertEquals(validationFormulas.calculateBedSpacing(0, null, null), 0);
		assertEquals(validationFormulas.calculateBedSpacing(3, 200.0, 30.0), 36.66, 0.01);
	}
}
