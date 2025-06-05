package com.sunseed.serviceImpl;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.Crop;
import com.sunseed.entity.FarquharParameter;
import com.sunseed.entity.OpticalProperty;
import com.sunseed.entity.StomatalParameter;
import com.sunseed.exceptions.BadRequestException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.CsvToXmlConverter;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.requestDTO.masterTables.CropRequestDto;
import com.sunseed.model.requestDTO.masterTables.OpticalRequestDto;
import com.sunseed.repository.CropRepository;
import com.sunseed.repository.OpticalPropertyRepository;
import com.sunseed.service.CropService;

@Service
public class CropServiceImpl implements CropService {

    @Autowired
    private CropRepository cropRepository;
    
    @Autowired
    private OpticalPropertyRepository opticalPropertyRepository;
    
    @Autowired
    private WebClientHelper webClientHelper;
//    private String fileUrl = "http://localhost:8090/simtool/v1/file";
    
    @Value("${simulation.url}")
    private String fileUrl;
    
//    @Value("${file.url}")
//    private String fileUrl = simulationUrl + "/simtool/v1/file";

    @Override
    public List<Crop> getCrops(String search) {

        if (search == null || search.trim().isEmpty()) {
            return cropRepository.findAllByOrderByNameAsc();
        } else {
            return cropRepository.findAllBySearchNameOrderByNameAsc(search);
        }

    }

    @Override
    public List<Crop> getCrops() {

        return cropRepository.findByIsActiveTrueAndHideFalseOrderByNameAsc();

    }

    @Override
    public List<Crop> getActiveCrops() {
        List<Crop> crops = cropRepository.findByIsActiveTrueAndHideFalseOrderByNameAsc();
        return crops;
    }

    @Override
    public Crop getCropById(Long cropId) {
        if (cropId == null || cropId <= 0)
            throw new UnprocessableException("crop.not.found");
        Optional<Crop> crop = cropRepository.findByIdAndIsActiveTrue(cropId);
        if (crop.isEmpty())
            throw new UnprocessableException("crop.not.found");
        return crop.get();
    }

    @Override
	public Crop addCrop(CropRequestDto requestDto, List<MultipartFile> opticalPropertyFiles) {
    	
    	if (opticalPropertyFiles == null || opticalPropertyFiles.isEmpty()) {
			throw new BadRequestException("Optical files are missing or not provided. Please upload valid files.");
		}
		boolean validFileFound = false;

    	OpticalProperty opticalProperty = createOpticalProperty(requestDto.getOpticalProperties());
    	
    	String trimmedName = requestDto.getName().trim();

	    // Check if the name contains only spaces or is empty after trimming
	    if (trimmedName.isEmpty()) {
	        throw new BadRequestException("Crop name is invalid. It cannot be empty or contain only spaces.");
	    }
	    
	    requestDto.setName(trimmedName);
			
			StomatalParameter stomatalParameter = StomatalParameter.builder()
					.em(requestDto.getEm()).io(requestDto.getIo()).k(requestDto.getK()).b(requestDto.getB()).build();
			FarquharParameter farquharParameter =  FarquharParameter.builder()
					.vcMax(requestDto.getVcMax()).jMax(requestDto.getJMax()).cjMax(requestDto.getCjMax())
					.haJMax(requestDto.getHajMax()).alpha(requestDto.getAlpha()).rd25(requestDto.getRd25()).build();

			
			for (MultipartFile file : opticalPropertyFiles) {
				String originalFilename = file.getOriginalFilename();
				if (originalFilename == null || originalFilename.trim().isEmpty()) {
					throw new BadRequestException("File name is invalid or empty.");
				}
				String fileExtension = getFileExtension(originalFilename).toLowerCase();
				if ( fileExtension.equals("csv")) {
					// Validate sheet presence in Excel file
					if (originalFilename != null) {
						// Proceed with further processing of the file
						validFileFound = true;
						System.out.println("Processing file: " + originalFilename);
						String fileName = processCsvToXml(file, requestDto.getName()); // Assuming reflectance for now
						opticalProperty.setOpticalPropertyFile(fileName);
						break;
					}

				} else if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
	                System.out.println("enter for reading excel file");
	                validFileFound = true;
	                System.out.println("Processing file: " + originalFilename);
	                String fileName = this.processExcelToXml(file, requestDto.getName());
	                opticalProperty.setOpticalPropertyFile(fileName);
	            }
	            else {
	                throw new BadRequestException(
	                        "Unsupported file format: " + fileExtension + ". Please upload .xls, or .xlsx files.");
	            }

			}

			if (!validFileFound) {
				throw new BadRequestException(
						"No valid optical files found. Files must contain sheets named 'reflectance' or 'transmission'.");
			}
			
		opticalProperty.setMasterType("Crop Layer");
//		Optional<Crop> optionalCrop = cropRepository.findByNameIgnoreCase(requestDto.getName());
//		if (optionalCrop.isPresent())
//			throw new ConflictException("crop.exists");
		Crop newCrop = Crop.builder().name(requestDto.getName())
				.requiredDLI(requestDto.getRequiredDLI())
				.requiredPPFD(requestDto.getRequiredPPFD())
				.harvestDays(requestDto.getHarvestDays())
				.f1(requestDto.getF1()).f2(requestDto.getF2()).f3(requestDto.getF3()).f4(requestDto.getF4())
				.f5(requestDto.getF5()).totalStagingCount(requestDto.getTotalStagingCount()).duration(requestDto.getDuration())
				.opticalProperty(opticalProperty).minStage(requestDto.getMinStage()).maxStage(requestDto.getMaxStage())
				.stomatalParameter(stomatalParameter)
				.farquharParameter(farquharParameter)
				.cropLabel(requestDto.getCropLabel())
				.hasPlantActualDate(requestDto.getHasPlantActualDate())
				.plantActualStartDate(requestDto.getPlantActualStartDate() != null ? requestDto.getPlantActualStartDate(): null)
				.plantMaxAge(requestDto.getPlantMaxAge())
				.maxPlantsPerBed(requestDto.getMaxPlantsPerBed())
				.build();
		
		Crop savedCrop = cropRepository.save(newCrop);
		opticalProperty.setMasterId(savedCrop.getId());
		opticalPropertyRepository.save(opticalProperty); // Save the updated OpticalProperty
		return savedCrop;
	}
    
 // excel to xml conversion
    private String processExcelToXml(MultipartFile excelFile, String labelName) {
        String cleanedFilePath = "";
        try {
            File xmlFile = CsvToXmlConverter.convertExcelToXml(excelFile, labelName,labelName+"_leaf_reflectivity_PAR", labelName+"_leaf_transmissivity_PAR");
            // Read all bytes from the XML file and convert to a String
//            String content = Files.readString(xmlFile.toPath());
//            System.out.println(content);

            String filePath = webClientHelper.postXmlFile(fileUrl+ "/simtool/v1/file", xmlFile);
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
//            .opticalPropertyFile(opticalDto.getReflectanceFile() != null ? opticalDto.getReflectanceFile().getOriginalFilename() : null)
            .build();
    }
    
	  
	  private String processCsvToXml(MultipartFile csvFile, String labelName) {
		  String cleanedFilePath = "";
	      try {
	          File xmlFile = CsvToXmlConverter.convertCsvToXml(csvFile, labelName);
	          String filePath = webClientHelper.postXmlFile(fileUrl+ "/simtool/v1/file", xmlFile);
	          cleanedFilePath = filePath.substring("File uploaded successfully: ".length());
	          System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      return cleanedFilePath;
	  }


    @Override
    public Crop updateCrop(CropRequestDto requestDto, Long cropId, List<MultipartFile> opticalPropertyFiles) {

        if (cropId == null || cropId <= 0)
            throw new UnprocessableException("crop.not.found");
        Optional<Crop> optionalCrop = cropRepository.findById(cropId);
        if (optionalCrop.isEmpty())
            throw new UnprocessableException("crop.not.found");
//        Long existingCropId = cropRepository.findIdWithNameIgnoreCase(requestDto.getName());
//        if (existingCropId != null && existingCropId != cropId)
//            throw new UnprocessableException("crop.exists");

        Crop existingCrop = optionalCrop.get();
        existingCrop.setName(requestDto.getName());
        existingCrop.setHide(requestDto.getHide());
        existingCrop.setDuration(requestDto.getDuration());
        existingCrop.setTotalStagingCount(requestDto.getTotalStagingCount());
        
        if(requestDto.getCropLabel() != null) {
        	existingCrop.setCropLabel(requestDto.getCropLabel());
        }
        
        OpticalProperty opticalProperty = existingCrop.getOpticalProperty();
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
	    
//	    StomatalParameter stomatalParameter = StomatalParameter.builder()
//                .em(requestDto.getEm()).io(requestDto.getIo()).k(requestDto.getK()).b(requestDto.getB()).build();
//        FarquharParameter farquharParameter = FarquharParameter.builder()
//                .vcMax(requestDto.getVcMax()).jMax(requestDto.getJMax()).cjMax(requestDto.getCjMax())
//                .haJMax(requestDto.getHajMax()).alpha(requestDto.getAlpha()).rd25(requestDto.getRd25()).build();
	    
	    StomatalParameter stomatalParameter = existingCrop.getStomatalParameter();
	    if (stomatalParameter == null) {
	        stomatalParameter = new StomatalParameter(); // Create new if not exists
	    }
	    stomatalParameter.setEm(requestDto.getEm());
	    stomatalParameter.setIo(requestDto.getIo());
	    stomatalParameter.setK(requestDto.getK());
	    stomatalParameter.setB(requestDto.getB());

	    // Handle FarquharParameter
	    FarquharParameter farquharParameter = existingCrop.getFarquharParameter();
	    if (farquharParameter == null) {
	        farquharParameter = new FarquharParameter(); // Create new if not exists
	    }
	    farquharParameter.setVcMax(requestDto.getVcMax());
	    farquharParameter.setJMax(requestDto.getJMax());
	    farquharParameter.setCjMax(requestDto.getCjMax());
	    farquharParameter.setHaJMax(requestDto.getHajMax());
	    farquharParameter.setAlpha(requestDto.getAlpha());
	    farquharParameter.setRd25(requestDto.getRd25());
	    
    	if(opticalPropertyFiles != null) {
    	for (MultipartFile file : opticalPropertyFiles) {
            String originalFilename = file.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename).toLowerCase();
            if (fileExtension.equals("csv")) {
                    // Proceed with further processing of the file
                    System.out.println("Processing file: " + originalFilename);
                    String fileName = processCsvToXml(file, requestDto.getName()); // Assuming reflectance for now
                    opticalProperty.setOpticalPropertyFile(fileName);
                }
            else if (file.getOriginalFilename().endsWith(".xls") || file.getOriginalFilename().endsWith(".xlsx")) {
                System.out.println("enter for reading excel file");
                System.out.println("Processing file: " + originalFilename);
                String fileName = this.processExcelToXml(file, requestDto.getName());
                opticalProperty.setOpticalPropertyFile(fileName);
            }
            else {
                throw new BadRequestException("Wrong format files are provided. Please upload valid files.");
            }
    	}
        }else {
        	opticalProperty.setOpticalPropertyFile(existingCrop.getOpticalProperty().getOpticalPropertyFile());
        }
    	

        
     // Update the new attributes (minStage, maxStage, duration)
        if (requestDto.getMinStage() != null) {
            existingCrop.setMinStage(requestDto.getMinStage());
        }
        if (requestDto.getMaxStage() != null) {
            existingCrop.setMaxStage(requestDto.getMaxStage());
        }
        if (requestDto.getDuration() != null) {
            // Validate duration is within range (1 to 365)
            if (requestDto.getDuration() < 1 || requestDto.getDuration() > 365) {
                throw new UnprocessableException("duration.must.be.in.range");
            }
            existingCrop.setDuration(requestDto.getDuration());
        }

        // You can also update other fields (e.g., vcMax, jMax, etc.) similarly if needed
        existingCrop.setRequiredDLI(requestDto.getRequiredDLI());
        existingCrop.setRequiredPPFD(requestDto.getRequiredPPFD());
        existingCrop.setHarvestDays(requestDto.getHarvestDays());
        existingCrop.setStomatalParameter(stomatalParameter);
        existingCrop.setFarquharParameter(farquharParameter);
        
        if(!existingCrop.getHasPlantActualDate().equals(requestDto.getHasPlantActualDate()))
        	existingCrop.setHasPlantActualDate(requestDto.getHasPlantActualDate());
//        if((!existingCrop.getPlantActualStartDate().equals(requestDto.getPlantActualStartDate())) ||
//        		existingCrop.getPlantActualStartDate() == null)
        existingCrop.setPlantActualStartDate(requestDto.getPlantActualStartDate() != null ? requestDto.getPlantActualStartDate(): null);
        if(!existingCrop.getPlantMaxAge().equals(requestDto.getPlantMaxAge()))
        	existingCrop.setPlantMaxAge(requestDto.getPlantMaxAge());
        if(!existingCrop.getMaxPlantsPerBed().equals(requestDto.getMaxPlantsPerBed()))
        	existingCrop.setMaxPlantsPerBed(requestDto.getMaxPlantsPerBed());

        // Update optical properties (if needed)
        if (requestDto.getOpticalProperties() != null) {
            existingCrop.setOpticalProperty(opticalProperty);
        }
//        if(requestDto.getF1() != null)
        	existingCrop.setF1(requestDto.getF1());
//        if(requestDto.getF2() != null)
        	existingCrop.setF2(requestDto.getF2());
//        if(requestDto.getF3() != null)
        	existingCrop.setF3(requestDto.getF3());
//        if(requestDto.getF4() != null)
        	existingCrop.setF4(requestDto.getF4());
//        if(requestDto.getF5() != null)
        	existingCrop.setF5(requestDto.getF5());
      

        Crop updatedCrop = cropRepository.save(existingCrop);
        return updatedCrop;
    }

    @Override
    public void deleteCrop(Long cropId) {

        if (cropId == null || cropId <= 0)
            throw new UnprocessableException("crop.not.found");

        Crop crop = cropRepository.findById(cropId).orElseThrow(() -> new UnprocessableException("crop.not.found"));

        crop.setIsActive(false);
        cropRepository.save(crop);
    }

}
