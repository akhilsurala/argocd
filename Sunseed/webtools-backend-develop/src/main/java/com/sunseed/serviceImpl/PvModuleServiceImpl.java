package com.sunseed.serviceImpl;

import java.util.List;
import java.util.Optional;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;

import com.sunseed.entity.OpticalProperty;
import com.sunseed.entity.PvModule;
import com.sunseed.exceptions.BadRequestException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.ResourceAlreadyExistsException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.CsvToXmlConverter;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.requestDTO.masterTables.PvModuleOpticalRequestDto;
import com.sunseed.model.requestDTO.masterTables.PvModuleRequestDto;
import com.sunseed.repository.OpticalPropertyRepository;
import com.sunseed.repository.PvModuleRepository;
import com.sunseed.service.PvModuleService;

@Service
public class PvModuleServiceImpl implements PvModuleService {

	@Autowired
	private PvModuleRepository pvModuleRepository;
	
	@Autowired
    private OpticalPropertyRepository opticalPropertyRepository;
	
	@Autowired
	private WebClientHelper webClientHelper;
//	private String fileUrl = "http://localhost:8090/simtool/v1/file";
	@Value("${simulation.url}")
    private String fileUrl;
    
//    @Value("${file.url}")
//    private String fileUrl = simulationUrl + "/simtool/v1/file";
	  

	@Override
	public List<PvModule> getPvModules() {
		List<PvModule> pvModules = pvModuleRepository.findAllByOrderByModuleTypeAsc();
		return pvModules;
	}
	
	@Override
	public List<PvModule> getPvModules(String search) {
		if (search == null || search.trim().isEmpty()) {
			return pvModuleRepository.findAllByOrderByModuleTypeAsc();
		} else {
			return pvModuleRepository.findAllBySearchOrderByNameAsc(search);
		}
	}

	@Override
	public List<PvModule> getActivePvModules() {
		List<PvModule> pvModules = pvModuleRepository.findByIsActiveTrueAndHideFalseOrderByModuleTypeAsc();
		return pvModules;
	}

	@Override
	public PvModule getPvModuleById(Long pvModuleId) {
		if (pvModuleId == null || pvModuleId <= 0)
			throw new UnprocessableException("pvmodule.not.found");
		Optional<PvModule> pvModule = pvModuleRepository.findByIdAndIsActiveTrue(pvModuleId);
		if (pvModule.isEmpty())
			throw new UnprocessableException("pvmodule.not.found");
		return pvModule.get();
	}

	public PvModule addPvModule(PvModuleRequestDto requestDto, List<MultipartFile> opticalPropertyFiles) {
		  
//		if (opticalPropertyFiles == null || opticalPropertyFiles.isEmpty()) {
//			throw new BadRequestException("Optical files are missing or not provided. Please upload valid files.");
//		}
		  
		  OpticalProperty frontOpticalProperty = null;
	      OpticalProperty backOpticalProperty = null;
	      
	      for (PvModuleOpticalRequestDto opticalDto : requestDto.getOpticalProperties()) {
	          OpticalProperty opticalProperty = createOpticalProperty(opticalDto);
	          boolean validFileFound = false;
	          String targetType = opticalDto.getType();

	          if (opticalPropertyFiles != null) {
	          for (MultipartFile file : opticalPropertyFiles) {
	              String originalFilename = file.getOriginalFilename();
	              
	              if (originalFilename != null && originalFilename.equalsIgnoreCase(targetType +".csv")) {
	                  // Check if the file contains sheets named "reflectance" or "transmission"
//	                  if (isSheetPresent(file, "reflectance") || isSheetPresent(file, "transmission")) {
//	                      // Proceed with further processing of the file
//	                      System.out.println("Processing file: " + originalFilename);
//	                      String fileName = processCsvToXml(file, requestDto.getManufacturerName() + targetType); // Assuming reflectance for now
//	                      opticalProperty.setOpticalPropertyFile(fileName); validFileFound = true;
//	                      validFileFound = true;
////	                      break;
//	  				} else {
//	  					throw new BadRequestException(
//	  							"Optical files are missing or not provided. Please upload valid files.");
//	  				}

	              }else if (originalFilename != null && originalFilename.contains(targetType) && (originalFilename.endsWith(".xls") || originalFilename.endsWith(".xlsx"))) {
	                    System.out.println("enter for reading excel file");
	                    validFileFound = true;
	                    System.out.println("Processing file: " + originalFilename);
	                    String fileName = this.processExcelToXml(file, requestDto.getManufacturerName() + targetType,targetType);
	                    opticalProperty.setOpticalPropertyFile(fileName);
	                }
//	              else {
//	            	  throw new BadRequestException(
//								"Optical files are missing or not provided. Please upload valid files.");
	              }
	          }else {
	            	opticalProperty.setOpticalPropertyFile("");
	            }
	            opticalProperty.setMasterType("PV Module");
	          if ("front".equalsIgnoreCase(opticalDto.getType())) {
	              frontOpticalProperty = opticalProperty;
	          } else if ("back".equalsIgnoreCase(opticalDto.getType())) {
	              backOpticalProperty = opticalProperty;
	          }
//	          if ((!validFileFound) && opticalPropertyFiles != null) {
//	                throw new BadRequestException(
//	                        "No valid optical files found. Files must contain sheets named 'reflectance' or 'transmission'.");
//	            }
	      }

		Optional<PvModule> optionalPvModule = pvModuleRepository.findByModuleTypeIgnoreCase(requestDto.getName());
		if (optionalPvModule.isPresent() && optionalPvModule.get().getPdc0().equals(requestDto.getPdc0()))
			throw new ConflictException("pvmodule.exists");
		PvModule newPvModule = PvModule.builder().moduleType(requestDto.getName()).length(requestDto.getLength())
				.width(requestDto.getWidth()).width(requestDto.getWidth())
		        .hide(requestDto.getHide())
		        .manufacturerName(requestDto.getManufacturerName())
		        .moduleName(requestDto.getModuleName())
		        .shortcode(requestDto.getShortcode())
		        .moduleTech(requestDto.getModuleTech())
		        .linkToDataSheet(requestDto.getLinkToDataSheet())
		        .numCellX(requestDto.getNumCellX())
		        .numCellY(requestDto.getNumCellY())
		        .longerSide(requestDto.getLongerSide())
		        .shorterSide(requestDto.getShorterSide())
		        .thickness(requestDto.getThickness())
		        .voidRatio(requestDto.getVoidRatio())
		        .xCell(requestDto.getXCell())
		        .yCell(requestDto.getYCell())
		        .xCellGap(requestDto.getXCellGap())
		        .yCellGap(requestDto.getYCellGap())
		        .vMap(requestDto.getVMap())
		        .iMap(requestDto.getIMap())
		        .idc0(requestDto.getIdc0())
		        .pdc0(requestDto.getPdc0())
		        .nEffective(requestDto.getNEffective())
		        .vOc(requestDto.getVOc())
		        .iSc(requestDto.getISc())
		        .alphaSc(requestDto.getAlphaSc())
		        .betaVoc(requestDto.getBetaVoc())
		        .gammaPdc(requestDto.getGammaPdc())
		        .temRef(requestDto.getTemRef())
		        .radSun(requestDto.getRadSun())
		        .f1(requestDto.getF1())
		        .f2(requestDto.getF2())
		        .f3(requestDto.getF3())
		        .f4(requestDto.getF4())
		        .f5(requestDto.getF5())
		        .frontOpticalProperty(frontOpticalProperty)
		        .backOpticalProperty(backOpticalProperty)
		        .build();

		PvModule savedPvModule = pvModuleRepository.save(newPvModule);
		if (frontOpticalProperty != null) {
            frontOpticalProperty.setMasterId(savedPvModule.getId());
            opticalPropertyRepository.save(frontOpticalProperty);
        }
        if (backOpticalProperty != null) {
            backOpticalProperty.setMasterId(savedPvModule.getId());
            opticalPropertyRepository.save(backOpticalProperty);
        }
		return savedPvModule;
	}
	
	private boolean isSheetPresent(MultipartFile file, String sheetName) {
	      try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
	          String line;
	          while ((line = br.readLine()) != null) {
	              if (line.toLowerCase().contains(sheetName.toLowerCase())) {
	                  return true;
	              }
	          }
	      } catch (Exception e) {
	          e.printStackTrace();
	      }
	      return false;
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
	  
	// excel to xml conversion
	    private String processExcelToXml(MultipartFile excelFile, String labelName, String type) {
	        String cleanedFilePath = "";
	        try {
	            File xmlFile = CsvToXmlConverter.convertExcelToXml(excelFile, labelName, "module_" + type + "_reflectivity_PAR", "module_" + type + "_transmissivity_PAR");
	            // Read all bytes from the XML file and convert to a String
//	            String content = Files.readString(xmlFile.toPath());
//	            System.out.println(content);

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


	  
	  private OpticalProperty createOpticalProperty(PvModuleOpticalRequestDto opticalDto) {
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

	@Override
	public PvModule updatePvModule(PvModuleRequestDto requestDto, Long pvModuleId, List<MultipartFile> opticalPropertyFiles) {

		if (pvModuleId == null || pvModuleId <= 0)
			throw new UnprocessableException("pvmodule.not.found");
		Optional<PvModule> existingModule = pvModuleRepository.findById(pvModuleId);
		if (existingModule.isEmpty())
			throw new UnprocessableException("pvmodule.not.found");
		// Long moduleId = pvModuleRepository.findIdWithModuleTypeIgnoreCase(requestDto.getName());
		// if (moduleId != null && moduleId != pvModuleId)
		// 	throw new ResourceAlreadyExistsException("Pv module already exists");
		
		List<PvModule> modulesWithSameName = pvModuleRepository.findAllByModuleTypeIgnoreCase(requestDto.getName());

		for (PvModule module : modulesWithSameName) {
		    if (!module.getId().equals(pvModuleId) &&
		        Objects.equals(module.getPdc0(), requestDto.getPdc0())) {
		        throw new ConflictException("pvmodule.exists");
		    }
		}

		PvModule existingPvModule = existingModule.get();
		
		OpticalProperty frontOpticalProperty = existingPvModule.getFrontOpticalProperty();
	    OpticalProperty backOpticalProperty = existingPvModule.getBackOpticalProperty();
	    
	    for (PvModuleOpticalRequestDto opticalDto : requestDto.getOpticalProperties()) {
//	        OpticalProperty opticalProperty = createOpticalProperty(opticalDto);
	        String targetType = opticalDto.getType();

	        boolean fileProcessed = false;
	        
	        OpticalProperty opticalProperty = "front".equalsIgnoreCase(targetType) ? frontOpticalProperty : backOpticalProperty;

	        if (opticalProperty == null) {
	            // If there's no existing OpticalProperty, create a new one (only if needed)
	            opticalProperty = createOpticalProperty(opticalDto);
	        }else {
	            // Update the existing OpticalProperty with new values
	            opticalProperty.setTransmissionNIR(opticalDto.getTransmissivity_NIR());
	            opticalProperty.setReflectionNIR(opticalDto.getReflectance_NIR());
	            opticalProperty.setTransmissionPAR(opticalDto.getTransmissivity_PAR());
	            opticalProperty.setReflectionPAR(opticalDto.getReflectance_PAR());
	        }

	        if (opticalPropertyFiles != null) {
	            for (MultipartFile file : opticalPropertyFiles) {
	                String originalFilename = file.getOriginalFilename();

	                if (originalFilename != null && (originalFilename.equalsIgnoreCase(targetType + ".csv"))) {
	                    // Check for required sheets
	                    if (isSheetPresent(file, "reflectance") || isSheetPresent(file, "transmission")) {
	                        String fileName = processCsvToXml(file, requestDto.getManufacturerName() + targetType);
	                        opticalProperty.setOpticalPropertyFile(fileName);
	                        fileProcessed = true;
	                    }
	                    else {
                            throw new BadRequestException(
                                    "Optical files are missing or not provided. Please upload valid files.1");
                        }

	                }
	                else if (originalFilename != null && originalFilename.equalsIgnoreCase(targetType + ".xls") || originalFilename.equalsIgnoreCase(targetType + ".xlsx")) {
                        System.out.println("enter for reading excel file");

                        System.out.println("Processing file: " + originalFilename);
                        String fileName = this.processExcelToXml(file, requestDto.getManufacturerName() + targetType,targetType);
                        opticalProperty.setOpticalPropertyFile(fileName);
                        fileProcessed = true;
                    }
	                
	            }
	        }
	        
	        

	        // Retain the existing file if no new file was processed
	        if ("front".equalsIgnoreCase(targetType)) {
	        	if (!fileProcessed && existingPvModule.getFrontOpticalProperty() != null) {
	                opticalProperty.setOpticalPropertyFile(existingPvModule.getFrontOpticalProperty().getOpticalPropertyFile());
	            }
	            frontOpticalProperty = opticalProperty;
	        } else if ("back".equalsIgnoreCase(targetType)) {
	            if (!fileProcessed && existingPvModule.getBackOpticalProperty() != null) {
	                opticalProperty.setOpticalPropertyFile(existingPvModule.getBackOpticalProperty().getOpticalPropertyFile());
	            }
	            backOpticalProperty = opticalProperty;
	        }
	    }

		existingPvModule.setFrontOpticalProperty(frontOpticalProperty != null ? frontOpticalProperty : existingPvModule.getFrontOpticalProperty());
	    existingPvModule.setBackOpticalProperty(backOpticalProperty != null ? backOpticalProperty : existingPvModule.getBackOpticalProperty());
		existingPvModule.setLength(requestDto.getLength());
		existingPvModule.setWidth(requestDto.getWidth());
		existingPvModule.setModuleType(requestDto.getName());
		existingPvModule.setHide(requestDto.getHide());
		existingPvModule.setManufacturerName(requestDto.getManufacturerName());
	    existingPvModule.setModuleName(requestDto.getModuleName());
	    existingPvModule.setShortcode(requestDto.getShortcode());
	    existingPvModule.setModuleTech(requestDto.getModuleTech());
	    existingPvModule.setLinkToDataSheet(requestDto.getLinkToDataSheet());
	    existingPvModule.setNumCellX(requestDto.getNumCellX());
	    existingPvModule.setNumCellY(requestDto.getNumCellY());
	    existingPvModule.setLongerSide(requestDto.getLongerSide());
	    existingPvModule.setShorterSide(requestDto.getShorterSide());
	    existingPvModule.setThickness(requestDto.getThickness());
	    existingPvModule.setVoidRatio(requestDto.getVoidRatio());
	    existingPvModule.setXCell(requestDto.getXCell());
	    existingPvModule.setYCell(requestDto.getYCell());
	    existingPvModule.setXCellGap(requestDto.getXCellGap());
	    existingPvModule.setYCellGap(requestDto.getYCellGap());
	    existingPvModule.setVMap(requestDto.getVMap());
	    existingPvModule.setIMap(requestDto.getIMap());
	    existingPvModule.setIdc0(requestDto.getIdc0());
	    existingPvModule.setPdc0(requestDto.getPdc0());
	    existingPvModule.setNEffective(requestDto.getNEffective());
	    existingPvModule.setVOc(requestDto.getVOc());
	    existingPvModule.setISc(requestDto.getISc());
	    existingPvModule.setAlphaSc(requestDto.getAlphaSc());
	    existingPvModule.setBetaVoc(requestDto.getBetaVoc());
	    existingPvModule.setGammaPdc(requestDto.getGammaPdc());
	    existingPvModule.setTemRef(requestDto.getTemRef());
	    existingPvModule.setRadSun(requestDto.getRadSun());
	    existingPvModule.setF1(requestDto.getF1());
	    existingPvModule.setF2(requestDto.getF2());
	    existingPvModule.setF3(requestDto.getF3());
	    existingPvModule.setF4(requestDto.getF4());
	    existingPvModule.setF5(requestDto.getF5());

		PvModule updatedPvModule = pvModuleRepository.save(existingPvModule);
		return updatedPvModule;
	}

	@Override
	public void deletePvModule(Long pvModuleId) {

		if (pvModuleId == null || pvModuleId <= 0)
			throw new UnprocessableException("pvmodule.not.found");
		
		// performing soft delete on pvModule
		PvModule pvModule = pvModuleRepository.findById(pvModuleId)
	            .orElseThrow(() -> new UnprocessableException("pvmodule.not.found"));
		
		pvModule.setIsActive(false);
        pvModuleRepository.save(pvModule);
	}

	

}
