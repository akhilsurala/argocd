package com.sunseed.simtool.serviceimpl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunseed.simtool.entity.GeoJson;
import com.sunseed.simtool.exception.InvalidRequestBodyArgumentException;
import com.sunseed.simtool.model.response.GeoJsonResponse;
import com.sunseed.simtool.model.response.GeoJsonResponse.Feature;
import com.sunseed.simtool.repository.GeoJsonRepository;
import com.sunseed.simtool.service.EPWService;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class EPWServiceImpl implements EPWService {
	
	@Value("${weather-data.source.url}")
	private String geoJsonDataSourceUrl;
	@Value("${epw.file-system.path}")
	private String epwFileSystemPath;
	@Value("${weather-data.api.key}")
	private String apiKey;
	
	@Value("${weather-data.source.url.fallback}")
	private String geoJsonDataSourceUrlFallback;
	
	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private GeoJsonRepository geoJsonRepository;

    private static final String[] HEADERS = {
            "year", "month", "day", "hour", "minute", "data_source_unct", "dry_bulb_temp",
            "dew_point_temp", "relative_humidity", "atmospheric_station_pressure", "ext_horiz_rad",
            "ext_direct_normal_rad", "horz_infrared_rad_intensity", "global_horz_rad",
            "direct_normal_rad", "diffuse_horz_rad", "global_horz_illuminance",
            "direct_normal_illuminance", "diffuse_horz_illuminance", "zenith_luminance",
            "wind_direction", "wind_speed", "total_sky_cover", "opaque_sky_cover", "visibility",
            "ceiling_height", "present_weather_observation", "present_weather_codes",
            "precipitable_water", "aerosol_optical_depth", "snow_depth", "days_since_last_snow",
            "albedo", "liquid_precipitation_depth", "liquid_precipitation_quantity"
    };

    @Override
    public List<WeatherData> getWeatherInfoForDateTime(Double longitude, Double latitude) throws IOException {
    	
    	log.debug("Fetching closest location");
    	GeoJson geoJson = getClosestLocationData(longitude, latitude);
    	log.debug("Closest cordinates lat: " + geoJson.getLatitude() + " long: " + geoJson.getLongitude() );
    	
//    	if(geoJson == null)
//    		throw new InvalidRequestBodyArgumentException("No weather data found for long: " + longitude + " lat: " + latitude);
    	
    	String fileName = epwFileSystemPath + "EPW_" + latitude + "_" + longitude + ".epw";
    	
    	//validate if file already exists in file system
    	if(fileAlreadyDownloaded("EPW_" + latitude + "_" + longitude + ".epw"))
    		log.debug("EPW file already exists, skipping download");
    	else
    	{
    		log.debug("Downloading epw file");
    		try {
    			downloadFile(geoJsonDataSourceUrl, fileName, longitude, latitude, apiKey);
    		}
    		catch (Exception e) {
    			log.debug("" + e);
    			log.info("Fetching epw file from fallback API");
				downloadFile(geoJson.getEpwFileUrl(), fileName);
			}
    	}
    		
        List<WeatherData> weatherDatas = readEPWFile(fileName, geoJson.getEpwFileUrl());

        if (!weatherDatas.isEmpty()) {
            
            return weatherDatas;
        } else {
            return null;
        }
    }
    
    private boolean fileAlreadyDownloaded(String fileName) {
    	Path directory = Paths.get(epwFileSystemPath);
        Path filePath = directory.resolve(fileName);
        
        if (Files.exists(filePath) && Files.isRegularFile(filePath))
        	return true;
        else
        	return false;
	}

	private GeoJson getClosestLocationData(Double longitude, Double latitude) {
		List<GeoJson> geoJsons = geoJsonRepository.findAll();
		GeoJson closestGeoJson = null;
		
		Double minDistance = Integer.MAX_VALUE * 1.0;
		for(GeoJson geoJson : geoJsons)
		{
			Double distance = Math.sqrt(Math.pow(Math.abs(longitude-geoJson.getLongitude()), 2) + Math.pow(Math.abs(latitude-geoJson.getLatitude()), 2));
			if(distance < minDistance)
			{
				minDistance = distance;
				closestGeoJson = geoJson;
			}
		}
		
		return closestGeoJson;
	}

	private void downloadFile(String source, String filePath) {
        File file = new File(filePath);
        
        // Create the parent directories if they do not exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
        	log.debug("Creating parent directory");
            parentDir.mkdirs();
        }
		
    	try (ReadableByteChannel readableByteChannel = Channels.newChannel(URI.create(source).toURL().openStream());
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                FileChannel fileChannel = fileOutputStream.getChannel()) {

               fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);

           } catch (IOException e) {
               e.printStackTrace();
           }
    }
	
	private void downloadFile(String apiUrl, String filePath, Double longitude, Double latitude, String apiKey) {
        File file = new File(filePath);

        // Create the parent directories if they do not exist
        File parentDir = file.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            parentDir.mkdirs();
        }

        try {
            String url = apiUrl + "?lat=" + latitude + "&lon=" + longitude;

            HttpHeaders headers = new HttpHeaders();
            headers.add("api-key", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
            
            try (FileOutputStream fileOutputStream = new FileOutputStream(filePath)) {
                fileOutputStream.write(response.getBody());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<WeatherData> readEPWFile(String filePath, String epwFileUrl) throws IOException {
        List<WeatherData> weatherDataList = new ArrayList<>();
        double timeZone = getTimeZoneFromEpw(filePath);

        try (InputStream inputStream = new FileInputStream(filePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withHeader(HEADERS).withSkipHeaderRecord(true))) {

            for (CSVRecord record : csvParser) {
                if (record.getRecordNumber() >= 8) { // Skip the first 8 lines
                	WeatherData data = new WeatherData(
                			timeZone,
                			epwFileUrl,
                            LocalDateTime.of(
                                    Integer.parseInt(record.get("year")),
                                    Integer.parseInt(record.get("month")),
                                    Integer.parseInt(record.get("day")),
                                    Math.floorMod(Integer.parseInt(record.get("hour")), 24),
                                    Math.floorMod(Integer.parseInt(record.get("minute")), 60)
                            ),
                            record.get("data_source_unct"),
                            Double.parseDouble(record.get("dry_bulb_temp")),
                            Double.parseDouble(record.get("dew_point_temp")),
                            Double.parseDouble(record.get("relative_humidity")),
                            Double.parseDouble(record.get("atmospheric_station_pressure")),
                            Double.parseDouble(record.get("ext_horiz_rad")),
                            Double.parseDouble(record.get("ext_direct_normal_rad")),
                            Double.parseDouble(record.get("horz_infrared_rad_intensity")),
                            Double.parseDouble(record.get("global_horz_rad")),
                            Double.parseDouble(record.get("direct_normal_rad")),
                            Double.parseDouble(record.get("diffuse_horz_rad")),
                            Double.parseDouble(record.get("global_horz_illuminance")),
                            Double.parseDouble(record.get("direct_normal_illuminance")),
                            Double.parseDouble(record.get("diffuse_horz_illuminance")),
                            Double.parseDouble(record.get("zenith_luminance")),
                            Double.parseDouble(record.get("wind_direction")),
                            Double.parseDouble(record.get("wind_speed")),
                            Double.parseDouble(record.get("total_sky_cover")),
                            Double.parseDouble(record.get("opaque_sky_cover")),
                            Double.parseDouble(record.get("visibility")),
                            Double.parseDouble(record.get("ceiling_height")),
                            record.get("present_weather_observation"),
                            record.get("present_weather_codes"),
                            Double.parseDouble(record.get("precipitable_water")),
                            Double.parseDouble(record.get("aerosol_optical_depth")),
                            Double.parseDouble(record.get("snow_depth")),
                            Integer.parseInt(record.get("days_since_last_snow")),
                            Double.parseDouble(record.get("albedo")),
                            Double.parseDouble(record.get("liquid_precipitation_depth")),
                            Double.parseDouble(record.get("liquid_precipitation_quantity"))
                    );
                    weatherDataList.add(data);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return weatherDataList;
    }
    
    public Double getTimeZoneFromEpw(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();  // Read the first line
            if (line != null && line.startsWith("LOCATION")) {
                String[] data = line.split(",");
                return Double.parseDouble(data[8]);  // The 9th element (index 8) is the timezone
            }
        }
        return null;
    }

    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class WeatherData {
    	private double timeZone;
    	private String dataSourceUrl;
    	private LocalDateTime dateTime;
        private String dataSourceUncertainty;
        private double airTemperature;
        private double dewPointTemp;
        private double airHumidity;
        private double airPressure;
        private double extHorizRad;
        private double extDirectNormalRad;
        private double horzInfraredRadIntensity;
        private double globalHorizRad;
        private double directNormalRad;
        private double diffuseHorizRad;
        private double globalHorizIlluminance;
        private double directNormalIlluminance;
        private double diffuseHorizIlluminance;
        private double zenithLuminance;
        private double windDirection;
        private double windSpeed;
        private double totalSkyCover;
        private double opaqueSkyCover;
        private double visibility;
        private double ceilingHeight;
        private String presentWeatherObservation;
        private String presentWeatherCodes;
        private double precipitableWater;
        private double aerosolOpticalDepth;
        private double snowDepth;
        private int daysSinceLastSnow;
        private double albedo;
        private double liquidPrecipitationDepth;
        private double liquidPrecipitationQuantity;
    }

	@Override
	public void getGeoJsonData() throws JsonMappingException, JsonProcessingException {
        String geoJsonString = restTemplate.getForObject(geoJsonDataSourceUrlFallback, String.class);
        
        log.debug("Fetched weather data");

        GeoJsonResponse geoJsonResponse = objectMapper.readValue(geoJsonString, GeoJsonResponse.class);
        
        saveGeoJsonToDB(geoJsonResponse.getFeatures());
	}

	private void saveGeoJsonToDB(List<Feature> features) {
		
		List<GeoJson> geoJsons = new ArrayList<>();
		
		features.forEach(feature -> {
			
			GeoJson geoJson = new GeoJson();
			geoJson.setLongitude(feature.getGeometry().getCoordinates()[0]);
			geoJson.setLatitude(feature.getGeometry().getCoordinates()[1]);
			geoJson.setTitle(feature.getProperties().getTitle());
			
			String epw = feature.getProperties().getEpw();
			geoJson.setEpwFileUrl(epw.substring(epw.indexOf("href=") + 5, epw.indexOf(">")));
			
			geoJsons.add(geoJson);
			
		});
		
		log.debug("Saving weather data into db");
		geoJsonRepository.saveAll(geoJsons);
	}
}
