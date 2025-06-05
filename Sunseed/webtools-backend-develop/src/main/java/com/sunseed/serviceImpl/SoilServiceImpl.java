package com.sunseed.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import com.sunseed.helper.CsvToXmlConverter;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.requestDTO.masterTables.OpticalRequestDto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.OpticalProperty;
import com.sunseed.entity.SoilType;
import com.sunseed.exceptions.BadRequestException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.model.requestDTO.masterTables.SoilRequestDto;
import com.sunseed.repository.OpticalPropertyRepository;
import com.sunseed.repository.SoilTypeRepo;
import com.sunseed.service.ImageService;
import com.sunseed.service.SoilService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SoilServiceImpl implements SoilService {

	private static final String folderName = "soil";
	private final SoilTypeRepo soilRepository;
	private final OpticalPropertyRepository opticalPropertyRepository;
	private final ImageService imageService;
	
	@Autowired
	private WebClientHelper webClientHelper;
//	private String fileUrl = "http://localhost:8090/simtool/v1/file";
	@Value("${simulation.url}")
    private String fileUrl;
    
//    @Value("${file.url}")
//    private String fileUrl = simulationUrl + "/simtool/v1/file";

	@Value("${project.image}")
	private String path;

	@Override
	public List<SoilType> getSoilDetails() {

		List<SoilType> soils = soilRepository.findAllByOrderBySoilNameAsc();
		return soils;
	}

	@Override
	public List<SoilType> getSoilDetails(String searchString) {
		if (searchString == null || searchString.trim().isEmpty()) {
			return soilRepository.findAllByOrderBySoilNameAsc();
		} else {
			return soilRepository.findAllBySearchOrderByNameAsc(searchString);
		}
	}

	@Override
	public List<SoilType> getActiveSoilDetails() {

		List<SoilType> soils = soilRepository.findByIsActiveTrueAndHideFalseOrderBySoilNameAsc();
		return soils;
	}

	@Override
	public SoilType getSoilById(Long soilId) {
		if (soilId == null || soilId <= 0)
			throw new UnprocessableException("soil.not.found");
		Optional<SoilType> soil = soilRepository.findByIdAndIsActiveTrue(soilId);
		if (soil.isEmpty())
			throw new UnprocessableException("soil.not.found");
		return soil.get();
	}

	@Override
	public SoilType addSoil(SoilRequestDto requestDto, MultipartFile image, List<MultipartFile> opticalPropertyFiles) {
		if (opticalPropertyFiles == null || opticalPropertyFiles.isEmpty()) {
			throw new BadRequestException("Optical files are missing or not provided. Please upload valid files.");
		}
		OpticalProperty opticalProperty = createOpticalProperty(requestDto.getOpticalProperties());
		boolean validFileFound = false;

		for (MultipartFile file : opticalPropertyFiles) {
			String originalFilename = file.getOriginalFilename();
			if (originalFilename == null || originalFilename.trim().isEmpty()) {
				throw new BadRequestException("File name is invalid or empty.");
			}
			String fileExtension = getFileExtension(originalFilename).toLowerCase();
			if (fileExtension.equals("csv")) {
				// Validate sheet presence in Excel file

//	          // Check if the file contains sheets named "reflectance" or "transmission"
//				if (isSheetPresent(file, "reflectance") && isSheetPresent(file, "transmission")) {
				// Proceed with further processing of the file
				validFileFound = true;
				System.out.println("Processing file: " + originalFilename);
				String fileName = processCsvToXml(file, requestDto.getName()); // Assuming reflectance for now
				opticalProperty.setOpticalPropertyFile(fileName);
				break;
			} else if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
				System.out.println("enter for reading excel file");
				validFileFound = true;
				System.out.println("Processing file: " + originalFilename);
				String fileName = this.processExcelToXml(file, requestDto.getName());
				opticalProperty.setOpticalPropertyFile(fileName);
			} else {
				throw new BadRequestException(
						"Unsupported file format: " + fileExtension + ". Please upload .xls, or .xlsx files.");
			}
		}

		// If no valid file was found, throw an exception
		if (!validFileFound) {
			throw new BadRequestException(
					"No valid optical files found. Files must contain sheets named 'reflectance' or 'transmission'.");
		}

		opticalProperty.setMasterType("Soil Layer");
		Optional<SoilType> optionalSoil = soilRepository.findBySoilNameIgnoreCase(requestDto.getName());
		if (optionalSoil.isPresent())
			throw new ConflictException("soil.exists");
		SoilType newSoil = SoilType.builder().soilName(requestDto.getName()).opticalProperty(opticalProperty).build();
		String imagePath = null;

		if (image != null) {

			try {
				// imagePath is path of the image saved in the file system.
				imagePath = this.imageService.uploadImageWithFolderNameAndFileName(path, image, folderName,
						newSoil.getSoilName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		newSoil.setSoilPicturePath(imagePath);

		SoilType savedSoil = soilRepository.save(newSoil);
		opticalProperty.setMasterId(savedSoil.getId());
	    opticalPropertyRepository.save(opticalProperty); // Save the updated OpticalProperty
		return savedSoil;
	}

	private String getFileExtension(String fileName) {
		int lastIndexOfDot = fileName.lastIndexOf('.');
		return (lastIndexOfDot != -1) ? fileName.substring(lastIndexOfDot + 1) : "";
	}

	private OpticalProperty createOpticalProperty(OpticalRequestDto opticalDto) {
		if (opticalDto == null) {
			return null;
		}

		// Build and return an OpticalProperty instance with null checks
		return OpticalProperty.builder()
				.reflectionPAR(opticalDto.getReflectance_PAR() != null ? opticalDto.getReflectance_PAR() : null)
				.reflectionNIR(opticalDto.getReflectance_NIR() != null ? opticalDto.getReflectance_NIR() : null)
				.transmissionPAR(opticalDto.getTransmissivity_PAR() != null ? opticalDto.getTransmissivity_PAR() : null)
				.transmissionNIR(opticalDto.getTransmissivity_NIR() != null ? opticalDto.getTransmissivity_NIR() : null)
//	          .opticalPropertyFile(opticalDto.getReflectanceFile() != null ? opticalDto.getReflectanceFile().getOriginalFilename() : null)
				.build();
	}

	private boolean isSheetPresent(MultipartFile file, String keyword) {
		try (InputStream inputStream = file.getInputStream()) {
			Workbook workbook = WorkbookFactory.create(inputStream); // Open the Excel workbook
			for (Sheet sheet : workbook) {
				if (sheet.getSheetName().toLowerCase().contains(keyword.toLowerCase())) {
					return true; // Return true if a matching sheet name is found
				}
			}
		} catch (Exception e) {
			e.printStackTrace(); // Handle exceptions gracefully
		}
		return false; // Return false if no matching sheet name is found
	}

	private String processCsvToXml(MultipartFile csvFile, String labelName) {
		String cleanedFilePath = "";
		try {
			File xmlFile = CsvToXmlConverter.convertCsvToXml(csvFile, labelName);
			String filePath = webClientHelper.postXmlFile(fileUrl + "/simtool/v1/file", xmlFile);
			cleanedFilePath = filePath.substring("File uploaded successfully: ".length());
			System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cleanedFilePath;
	}

	// excel to xml conversion
	private String processExcelToXml(MultipartFile excelFile, String labelName) {
		String cleanedFilePath = "";
		try {
			File xmlFile = CsvToXmlConverter.convertExcelToXml(excelFile, labelName
					,labelName+"_reflectivity_PAR",labelName+"_transmissivity_PAR");
			// Read all bytes from the XML file and convert to a String
//		            String content = Files.readString(xmlFile.toPath());
//		            System.out.println(content);

			String filePath = webClientHelper.postXmlFile(fileUrl + "/simtool/v1/file", xmlFile);
			cleanedFilePath = filePath.substring("File uploaded successfully: ".length());
			System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
		} catch (IllegalArgumentException e) {
            // Preserve specific validation error messages
            throw new BadRequestException(e.getMessage());    
        } catch (Exception e) {
			e.printStackTrace();
			throw new BadRequestException(
					"File doesn't contain transmittance and refectance data. Please upload valid files.");
		}
		return cleanedFilePath;
	}


	@Override
	public SoilType updateSoil(SoilRequestDto requestDto, Long soilId, MultipartFile image,
			List<MultipartFile> opticalPropertyFiles) {
		if (soilId == null || soilId <= 0)
			throw new UnprocessableException("soil.not.found");
		Optional<SoilType> optionalSoil = soilRepository.findById(soilId);
		if (optionalSoil.isEmpty())
			throw new UnprocessableException("soil.not.found");
		Long existingSoilId = soilRepository.findIdWithSoilNameIgnoreCase(requestDto.getName());
		if (existingSoilId != null && existingSoilId != soilId)
			throw new UnprocessableException("soil.exists");

		SoilType existingSoil = optionalSoil.get();
		OpticalProperty opticalProperty = existingSoil.getOpticalProperty();
	    if (opticalProperty == null) {
	        opticalProperty = new OpticalProperty(); // Create new if not exists
	    }
	    
	    if (requestDto.getOpticalProperties() != null) {
	        // Update fields of opticalProperty
	        opticalProperty.setReflectionNIR(requestDto.getOpticalProperties().getReflectance_NIR());
	        opticalProperty.setReflectionPAR(requestDto.getOpticalProperties().getReflectance_PAR());
	        opticalProperty.setTransmissionNIR(requestDto.getOpticalProperties().getTransmissivity_NIR());
	        opticalProperty.setTransmissionPAR(requestDto.getOpticalProperties().getTransmissivity_PAR());
	    }
	    
		if (opticalPropertyFiles != null) {
			for (MultipartFile file : opticalPropertyFiles) {
				String originalFilename = file.getOriginalFilename();

				String fileExtension = getFileExtension(originalFilename).toLowerCase();
				if (fileExtension.equals("csv")) {
					System.out.println("Processing file: " + originalFilename);
					String fileName = processCsvToXml(file, requestDto.getName()); // Assuming reflectance for now
					opticalProperty.setOpticalPropertyFile(fileName);
					break;
				} else if (file.getOriginalFilename().endsWith(".xls")
						|| file.getOriginalFilename().endsWith(".xlsx")) {
					System.out.println("enter for reading excel file");
					System.out.println("Processing file: " + originalFilename);
					String fileName = this.processExcelToXml(file, requestDto.getName());
					opticalProperty.setOpticalPropertyFile(fileName);
				} else {
					throw new BadRequestException("Wrong format files are provided. Please upload valid files.");
				}

			}
		} else {
			opticalProperty.setOpticalPropertyFile(existingSoil.getOpticalProperty().getOpticalPropertyFile());
		}
		existingSoil.setSoilName(requestDto.getName());
		existingSoil.setOpticalProperty(opticalProperty);
		String imagePath = null;
		if (image != null) {
			try {
				// imagePath is path of the image saved in the file system.
				imagePath = this.imageService.uploadImageWithFolderNameAndFileName(path, image, folderName,
						existingSoil.getSoilName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		existingSoil.setSoilPicturePath(imagePath);
		existingSoil.setHide(requestDto.getHide());

		SoilType updatedSoil = soilRepository.save(existingSoil);
		return updatedSoil;
	}

	@Override
	public void deleteSoil(Long soilId) {

		if (soilId == null || soilId <= 0)
			throw new UnprocessableException("soil.not.found");

		SoilType soil = soilRepository.findById(soilId).orElseThrow(() -> new UnprocessableException("soil.not.found"));

		soil.setIsActive(false);
		soilRepository.save(soil);
	}

}
