package com.sunseed.serviceImpl;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.sunseed.entity.OpticalProperty;
import com.sunseed.entity.ProtectionLayer;
import com.sunseed.exceptions.BadRequestException;
import com.sunseed.exceptions.ConflictException;
import com.sunseed.exceptions.UnprocessableException;
import com.sunseed.helper.CsvToXmlConverter;
import com.sunseed.helper.WebClientHelper;
import com.sunseed.model.requestDTO.masterTables.OpticalRequestDto;
import com.sunseed.model.requestDTO.masterTables.ProtectionLayerRequestDto;
import com.sunseed.repository.OpticalPropertyRepository;
import com.sunseed.repository.ProtectionLayerRepo;
import com.sunseed.service.ProtectionLayerService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProtectionLayerServiceImpl implements ProtectionLayerService {

	private final ProtectionLayerRepo protectionLayerRepository;
	
	private final OpticalPropertyRepository opticalPropertyRepository;
	
	@Autowired
	private WebClientHelper webClientHelper;
//	private String fileUrl = "http://localhost:8090/simtool/v1/file";
	@Value("${simulation.url}")
    private String fileUrl;
    
//    @Value("${file.url}")
//    private String fileUrl = simulationUrl + "/simtool/v1/file";

	@Override
	public List<ProtectionLayer> getProtectionLayers() {
		List<ProtectionLayer> protectionLayers = protectionLayerRepository.findAllByOrderByProtectionLayerNameAsc();
		return protectionLayers;
	}

	@Override
	public List<ProtectionLayer> getProtectionLayers(String search) {
		if (search == null || search.trim().isEmpty()) {
			return protectionLayerRepository.findAllByOrderByProtectionLayerNameAsc();
		} else {
			return protectionLayerRepository.findAllBySearchOrderByNameAsc(search);
		}
	}

	@Override
	public List<ProtectionLayer> getActiveProtectionLayers() {
		List<ProtectionLayer> protectionLayers = protectionLayerRepository
				.findByIsActiveTrueAndHideFalseOrderByProtectionLayerNameAsc();
		return protectionLayers;
	}

	@Override
	public ProtectionLayer getProtectionLayerById(Long protectionLayerId) {
		if (protectionLayerId == null || protectionLayerId <= 0)
			throw new UnprocessableException("protectionLayer.not.found");
		Optional<ProtectionLayer> protectionLayer = protectionLayerRepository
				.findByProtectionLayerIdAndIsActiveTrue(protectionLayerId);
		if (protectionLayer.isEmpty())
			throw new UnprocessableException("protectionLayer.not.found");
		return protectionLayer.get();
	}

	@Override
	 public ProtectionLayer addProtectionLayer(ProtectionLayerRequestDto requestDto, List<MultipartFile> opticalPropertyFiles, MultipartFile texture) {

		OpticalProperty opticalProperty = createOpticalProperty(requestDto.getOpticalProperties());
		boolean validFileFound = false;
		if (opticalPropertyFiles != null) {
			for (MultipartFile file : opticalPropertyFiles) {
				String originalFilename = file.getOriginalFilename();
				System.out.println("originalFilename" + originalFilename);
				if (originalFilename == null || originalFilename.trim().isEmpty()) {
					throw new BadRequestException("File name is invalid or empty.");
				}
				String fileExtension = getFileExtension(originalFilename).toLowerCase();
				System.out.println("file Extension is :" + fileExtension);
				if (fileExtension.equals("csv")) {
//                validFileFound = true;
					System.out.println("Processing file: " + originalFilename);
					String fileName = processCsvToXml(file, requestDto.getName()); // Assuming reflectance for now
					opticalProperty.setOpticalPropertyFile(fileName);
					break;

				} else if (file.getOriginalFilename().endsWith(".xls")
						|| file.getOriginalFilename().endsWith(".xlsx")) {
					System.out.println("enter for reading excel file");
//                validFileFound = true;
					System.out.println("Processing file: " + originalFilename);
					String fileName = this.processExcelToXml(file, requestDto.getName());
					opticalProperty.setOpticalPropertyFile(fileName);
				} else {
					throw new BadRequestException(
							"Unsupported file format: " + fileExtension + ". Please upload .xls, or .xlsx files.");
				}

			}
		} else {
			opticalProperty.setOpticalPropertyFile(null);
		}
		
		
		
		String cleanedFilePath = "";
		try {
			if(texture != null) {
				String textureUrl = webClientHelper.postMultipartFile(fileUrl+ "/simtool/v1/file", texture);
				cleanedFilePath = textureUrl.substring("File uploaded successfully: ".length());
				opticalProperty.setLinkToTexture(cleanedFilePath);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		opticalProperty.setMasterType("Protection Layer");
		Optional<ProtectionLayer> optionalProtectionLayer = protectionLayerRepository
				.findByProtectionLayerNameIgnoreCase(requestDto.getName());
		if (optionalProtectionLayer.isPresent())
			throw new ConflictException("protectionLayer.exists");
		 ProtectionLayer newProtectionLayer = ProtectionLayer.builder().protectionLayerName(requestDto.getName())
		    		.polysheets(requestDto.getPolysheets())
		            .linkToTexture(cleanedFilePath)
		            .diffusionFraction(requestDto.getDiffusionFraction())
		            .transmissionPercentage(requestDto.getTransmissionPercentage())
		            .voidPercentage(requestDto.getVoidPercentage())
		            .opticalProperty(opticalProperty)
		            .f1(requestDto.getF1())
		            .f2(requestDto.getF2())
		            .f3(requestDto.getF3())
		            .f4(requestDto.getF4())
		            .build();

		ProtectionLayer savedProtectionLayer = protectionLayerRepository.save(newProtectionLayer);
		opticalProperty.setMasterId(savedProtectionLayer.getProtectionLayerId());
	    opticalPropertyRepository.save(opticalProperty); // Save the updated OpticalProperty
		return savedProtectionLayer;
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
	
	// excel to xml conversion
    private String processExcelToXml(MultipartFile excelFile, String labelName) {
        String cleanedFilePath = "";
        try {
            File xmlFile = CsvToXmlConverter.convertExcelToXml(excelFile, labelName,labelName+"_reflectivity_PAR", labelName+"_transmissivity_PAR");
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
		          String filePath = webClientHelper.postXmlFile(fileUrl+ "/simtool/v1/file", xmlFile);
		          cleanedFilePath = filePath.substring("File uploaded successfully: ".length());
		          System.out.println("Generated XML: " + xmlFile.getAbsolutePath());
		      } catch (Exception e) {
		          e.printStackTrace();
		      }
		      return cleanedFilePath;
		  }


	@Override
	public ProtectionLayer updateProtectionLayer(ProtectionLayerRequestDto requestDto, Long protectionLayerId, List<MultipartFile> opticalPropertyFiles, MultipartFile texture) {

		if (protectionLayerId == null || protectionLayerId <= 0)
			throw new UnprocessableException("protectionLayer.not.found");
		Optional<ProtectionLayer> optionalProtectionLayer = protectionLayerRepository.findById(protectionLayerId);
		if (optionalProtectionLayer.isEmpty())
			throw new UnprocessableException("protectionLayer.not.found");
		Long existingProtectionLayerId = protectionLayerRepository
				.findIdWithProtectionLayerNameIgnoreCase(requestDto.getName());
		if (existingProtectionLayerId != null && existingProtectionLayerId != protectionLayerId)
			throw new UnprocessableException("protectionLayer.exists");

		ProtectionLayer existingProtectionLayer = optionalProtectionLayer.get();
		existingProtectionLayer.setProtectionLayerName(requestDto.getName());
		existingProtectionLayer.setHide(requestDto.getHide());
		
		OpticalProperty opticalProperty = existingProtectionLayer.getOpticalProperty();
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
		} else {
			opticalProperty
					.setOpticalPropertyFile(existingProtectionLayer.getOpticalProperty().getOpticalPropertyFile());
		}

		if (texture != null) {
			String cleanedFilePath = "";
			try {
				String textureUrl = webClientHelper.postMultipartFile(fileUrl+ "/simtool/v1/file", texture);
				cleanedFilePath = textureUrl.substring("File uploaded successfully: ".length());
				opticalProperty.setLinkToTexture(cleanedFilePath);

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			existingProtectionLayer.setLinkToTexture(cleanedFilePath);
		} else {
			existingProtectionLayer.setLinkToTexture(existingProtectionLayer.getLinkToTexture());
			if(opticalProperty.getLinkToTexture() != null) {
				opticalProperty.setLinkToTexture(opticalProperty.getLinkToTexture());
			}
			else {
				opticalProperty.setLinkToTexture(requestDto.getLinkToTexture());
			}
		}

		existingProtectionLayer.setPolysheets(requestDto.getPolysheets());
//		existingProtectionLayer.setLinkToTexture(requestDto.getLinkToTexture());
		existingProtectionLayer.setDiffusionFraction(requestDto.getDiffusionFraction());
		existingProtectionLayer.setTransmissionPercentage(requestDto.getTransmissionPercentage());
		existingProtectionLayer.setVoidPercentage(requestDto.getVoidPercentage());
//        existingProtectionLayer.setOpticalProperties(opticalPropertiesMapper.map(requestDto.getOpticalProperties()));

		// Update optical properties (if needed)
		if (requestDto.getOpticalProperties() != null) {
			existingProtectionLayer.setOpticalProperty(opticalProperty);
		}
//		if (requestDto.getF1() != null)
			existingProtectionLayer.setF1(requestDto.getF1());
//		if (requestDto.getF2() != null)
			existingProtectionLayer.setF2(requestDto.getF2());
//		if (requestDto.getF3() != null)
			existingProtectionLayer.setF3(requestDto.getF3());
//		if (requestDto.getF4() != null)
			existingProtectionLayer.setF4(requestDto.getF4());

		ProtectionLayer updatedProtectionLayer = protectionLayerRepository.save(existingProtectionLayer);
		return updatedProtectionLayer;
	}

	@Override
	public void deleteProtectionLayer(Long protectionLayerId) {

		if (protectionLayerId == null || protectionLayerId <= 0)
			throw new UnprocessableException("protectionLayer.not.found");

		// performing soft delete on pvModule
		ProtectionLayer protectionLayer = protectionLayerRepository.findById(protectionLayerId)
				.orElseThrow(() -> new UnprocessableException("protectionLayer.not.found"));

		protectionLayer.setIsActive(false);
		protectionLayerRepository.save(protectionLayer);
	}

}
