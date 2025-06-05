package com.sunseed.simtool.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.junit.jupiter.api.Test;

public class LogUtilsTest {
	
	@Test
	public void testClassInit()
	{
		assertThat(new LogUtils()).isNotNull();
	}

	@Test
	public void testExtractNumericalValue() {
		String logs = "some logs\nsome logs\npv_yield : 456.78\nfront_gain : 123\nrear_gain : 789.01\ntilt_angle : NA\nsome logs";

        assertEquals("456.78", LogUtils.extractNumericalValue(logs, "pv_yield"));
        assertEquals("123", LogUtils.extractNumericalValue(logs, "front_gain"));
        assertEquals("789.01", LogUtils.extractNumericalValue(logs, "rear_gain"));
        assertEquals(null, LogUtils.extractNumericalValue(logs, "albedo"));
        assertEquals(null, LogUtils.extractNumericalValue(logs, "tilt_angle"));
	}
	
	@Test
    public void testExtractUrlValue() {
        String logs = "some logs\nurl_key : https://example.com\nanother_url_key : http://example.org\nurl: \nsome logs";

        assertEquals("https://example.com", LogUtils.extractUrlValue(logs, "url_key"));
        assertEquals("http://example.org", LogUtils.extractUrlValue(logs, "another_url_key"));
        assertEquals(null, LogUtils.extractUrlValue(logs, "missing_url_key"));
        assertEquals(null, LogUtils.extractUrlValue(logs, "url"));
    }
	
	@Test
	public void testExtractCarbonAssimilationValues() {
		String logs = "some logs\ncarbon_assimilation_corn_1 : -23.45\ncarbon_assimilation_wheat_2:34.56\nsome logs\n";

        Map<Integer,Map<String, Double>> carbonAssimilations = LogUtils.extractCarbonAssimilationValues(logs);
        
        assertNotNull(carbonAssimilations);
        assertFalse(carbonAssimilations.isEmpty());
        assertEquals(carbonAssimilations.size(), 2);
        assertTrue(carbonAssimilations.containsKey(1));
        assertTrue(carbonAssimilations.containsKey(1));
        assertEquals(carbonAssimilations.get(1).get("corn"), -23.45);
        assertEquals(carbonAssimilations.get(2).get("wheat"), 34.56);
	}
	
	@Test
	public void testExtractCarbonAssimilationValues_noValues() {
		String logs = "some logs\nsome logs\npv_yield : 456.78\nfront_gain : 123\nrear_gain : 789.01\ntilt_angle : NA\nsome logs";

        Map<Integer, Map<String, Double>> carbonAssimilations = LogUtils.extractCarbonAssimilationValues(logs);
        
        assertNull(carbonAssimilations);
	}
	
	@Test
	public void test_saveSimulationResult()
	{
		LogUtils.saveSimulationResult(Map.of("key","value"));
	}
}
