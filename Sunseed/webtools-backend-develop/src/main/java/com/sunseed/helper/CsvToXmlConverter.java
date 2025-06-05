package com.sunseed.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

//File xmlFile = new File(System.getProperty("java.io.tmpdir"), labelName + ".xml");
public class CsvToXmlConverter {

	public static File convertCsvToXml(MultipartFile csvFile, String labelName) throws IOException, ParserConfigurationException, TransformerException {
	    // Specify the directory where you want to save the XML file
		File xmlFile = new File(System.getProperty("java.io.tmpdir"), labelName + ".xml");
	    // Initialize XML Document
	    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
	    Document document = documentBuilder.newDocument();

	    // Root element
	    Element root = document.createElement("helios");
	    document.appendChild(root);

	    // Read and process CSV
	    try (BufferedReader br = new BufferedReader(new java.io.InputStreamReader(csvFile.getInputStream()))) {
	        String line;
	        Element reflectanceElement = null;
	        Element transmittanceElement = null;

	        // Read the first line to determine the type of data (Reflectance/Transmittance)
	        String firstLine = br.readLine();
	        String typeOfData = "";

	        // Check if the first line contains the keywords "reflectance" or "transmittance"
	        if (firstLine != null) {
	            if (firstLine.toLowerCase().contains("reflectance")) {
	                typeOfData += "Reflectance, ";
	            }
	            if (firstLine.toLowerCase().contains("transmittance")) {
	                typeOfData += "Transmittance, ";
	            }

	            // Remove the trailing comma and space
	            if (typeOfData.endsWith(", ")) {
	                typeOfData = typeOfData.substring(0, typeOfData.length() - 2);
	            }
	        }

	        // Create XML elements for Reflectance and Transmittance if they exist
	        if (!typeOfData.isEmpty()) {
	            if (typeOfData.contains("Reflectance")) {
	                reflectanceElement = document.createElement("globaldata_vec2");
	                reflectanceElement.setAttribute("label", labelName + "_reflectance");
	                root.appendChild(reflectanceElement);
	            }
	            if (typeOfData.contains("Transmittance")) {
	                transmittanceElement = document.createElement("globaldata_vec2");
	                transmittanceElement.setAttribute("label", labelName + "_transmittance");
	                root.appendChild(transmittanceElement);
	            }
	        }

	        // Process the data lines starting from the second line
	        while ((line = br.readLine()) != null) {
	            line = line.trim();

	            // Skip if empty line
	            if (line.isEmpty()) continue;

	            // Process data lines as "wavelength value"
	            String[] parts = line.split(",");
	            if (parts.length >= 2) {
	                String wavelength = parts[0].trim();
	                String value = parts[1].trim();

	                // Add the data to the appropriate XML element
	                if (reflectanceElement != null) {
	                    // Create a new text node with tab-separated values
	                    Text dataText = document.createTextNode(wavelength + "\t" + value);
	                    reflectanceElement.appendChild(dataText);
	                    // Create a newline character for the next line
	                    reflectanceElement.appendChild(document.createTextNode("\n"));
	                }
	                if (transmittanceElement != null) {
	                    // Create a new text node with tab-separated values
	                    Text dataText = document.createTextNode(wavelength + "\t" + value);
	                    transmittanceElement.appendChild(dataText);
	                    // Create a newline character for the next line
	                    transmittanceElement.appendChild(document.createTextNode("\n"));
	                }
	            }
	        }
	    }

	    // Write XML to file
	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    DOMSource domSource = new DOMSource(document);
	    StreamResult streamResult = new StreamResult(xmlFile);

	    transformer.transform(domSource, streamResult);

	    return xmlFile;
	}
	




	// convert xls or xlsx into xml

//	public static File convertExcelToXml(MultipartFile excelFile, String propertyName
//			,String reflectivityLabel,String transmissivityLabel) throws IOException, ParserConfigurationException, TransformerException {
//		// Specify the directory where you want to save the XML file
//		File xmlFile = new File(System.getProperty("java.io.tmpdir"), propertyName + ".xml");
//
//		// Initialize XML Document
//		DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
//		DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
//		Document document = documentBuilder.newDocument();
//
//		// Root element
//		Element root = document.createElement("helios");
//		document.appendChild(root);
//
//		// Read and process Excel
//		try (Workbook workbook = WorkbookFactory.create(excelFile.getInputStream())) {
//			
//			List<String> sheetNames = new ArrayList<>();
//	        boolean containsReflectance = false;
//	        boolean containsTransmittance = false;
//
//	        // Collect all sheet names and check for required sheets
//	        for (Sheet sheet : workbook) {
//	            String sheetName = sheet.getSheetName().toLowerCase();
//	            sheetNames.add(sheetName);
//	            if ("reflectance".equalsIgnoreCase(sheetName)) {
//	                containsReflectance = true;
//	            } else if ("transmittance".equalsIgnoreCase(sheetName)) {
//	                containsTransmittance = true;
//	            }
//	        }
//
//	        // Validate that at least one required sheet exists
//	        if (!containsReflectance && !containsTransmittance) {
//	            throw new IllegalArgumentException("File doesn't contain either 'reflectance' or 'transmittance' sheet.");
//	        }
//			
//			// Iterate over all sheets in the workbook
//			for (Sheet sheet : workbook) {
//				if ("reflectance".equalsIgnoreCase(sheet.getSheetName())) {
//					// Reflectance sheet
//					Element reflectanceElement = document.createElement("globaldata_vec2");
//					reflectanceElement.setAttribute("label",  reflectivityLabel);
//					root.appendChild(reflectanceElement);
//
//					// Process rows for reflectance
//					for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//						Row row = sheet.getRow(i);
//						if (row == null) continue;
//
//						String wavelength = getCellValueAsString(row.getCell(0));
//						String reflectance = getCellValueAsString(row.getCell(1));
//						
//						// Skip empty rows (both values must be non-empty to add)
//	                    if ((wavelength == null || wavelength.trim().isEmpty()) &&
//	                        (reflectance == null || reflectance.trim().isEmpty())) {
//	                        continue;
//	                    }
//	                    
//	                    if(i == 1) {
//	                    	reflectanceElement.appendChild(document.createTextNode("\n"));
//	                    }
//
//
//						// Create text node with wavelength and reflectance values
//						String data = wavelength + "\t" + reflectance ;
//						Text dataText = document.createTextNode(data);
//						reflectanceElement.appendChild(dataText);
//						// Append a <br> tag for a new line
////						Element br = document.createElement("br");
////						reflectanceElement.appendChild(br);
//						reflectanceElement.appendChild(document.createTextNode("\n"));
//					}
//				} else if ("transmittance".equalsIgnoreCase(sheet.getSheetName())) {
//					// Transmittance sheet
//					Element transmittanceElement = document.createElement("globaldata_vec2");
//					transmittanceElement.setAttribute("label",  transmissivityLabel);
//					root.appendChild(transmittanceElement);
//
//					// Process rows for transmittance
//					for (int i = 1; i <= sheet.getLastRowNum(); i++) {
//						Row row = sheet.getRow(i);
//						if (row == null) continue;
//
//						String wavelength = getCellValueAsString(row.getCell(0));
//						String transmittance = getCellValueAsString(row.getCell(1));
//						
//						// Skip empty rows (both values must be non-empty to add)
//	                    if ((wavelength == null || wavelength.trim().isEmpty()) &&
//	                        (transmittance == null || transmittance.trim().isEmpty())) {
//	                        continue;
//	                    }
//	                    
//	                    if(i == 1) {
//	                    	transmittanceElement.appendChild(document.createTextNode("\n"));
//	                    }
//
//						// Create text node with wavelength and transmittance values
//						String data = wavelength + "\t" + transmittance ;
//						Text dataText = document.createTextNode(data);
//						transmittanceElement.appendChild(dataText);
//						// Append a <br> tag for a new line
////						Element br = document.createElement("br");
////						transmittanceElement.appendChild(br);
//						transmittanceElement.appendChild(document.createTextNode("\n"));
//					}
//				}
//			}
//		}
//
//		// Write XML to file
//		TransformerFactory transformerFactory = TransformerFactory.newInstance();
//		Transformer transformer = transformerFactory.newTransformer();
//		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
//		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
//		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//		DOMSource domSource = new DOMSource(document);
//		StreamResult streamResult = new StreamResult(xmlFile);
//		transformer.transform(domSource, streamResult);
//
//		return xmlFile;
//	}
	
	public static File convertExcelToXml(MultipartFile excelFile, String propertyName, String reflectivityLabel, String transmissivityLabel) throws IOException, ParserConfigurationException, TransformerException {
	    File xmlFile = new File(System.getProperty("java.io.tmpdir"), propertyName + ".xml");
	    DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
	    DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
	    Document document = documentBuilder.newDocument();
	    Element root = document.createElement("helios");
	    document.appendChild(root);
	    
	    List<String> missingReflectance = new ArrayList<>();
	    List<String> missingTransmittance = new ArrayList<>();
	    List<String> invalidReflectance = new ArrayList<>();
	    List<String> invalidTransmittance = new ArrayList<>();
	    List<String> sumExceedingWavelengths = new ArrayList<>();
	    List<String> missingWavelengthReflectance = new ArrayList<>();
	    List<String> missingWavelengthTransmittance = new ArrayList<>();

	    try (Workbook workbook = WorkbookFactory.create(excelFile.getInputStream())) {
	        boolean containsReflectance = false;
	        boolean containsTransmittance = false;

	        for (Sheet sheet : workbook) {
	            String sheetName = sheet.getSheetName().toLowerCase();
	            if ("reflectance".equalsIgnoreCase(sheetName)) containsReflectance = true;
	            if ("transmittance".equalsIgnoreCase(sheetName)) containsTransmittance = true;
	        }

	        if (!containsReflectance && !containsTransmittance) {
	            throw new IllegalArgumentException("File doesn't contain either 'reflectance' or 'transmittance' sheet.");
	        }
	        
	        Map<String, Double> reflectanceMap = new HashMap<>();
	        Map<String, Double> transmittanceMap = new HashMap<>();

	        for (Sheet sheet : workbook) {
	            if ("reflectance".equalsIgnoreCase(sheet.getSheetName())) {
	                Element reflectanceElement = document.createElement("globaldata_vec2");
	                reflectanceElement.setAttribute("label", reflectivityLabel);
	                root.appendChild(reflectanceElement);

	                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	                    Row row = sheet.getRow(i);
	                    if (row == null) continue;

	                    String wavelength = getCellValueAsString(row.getCell(0));
	                    String reflectanceStr = getCellValueAsString(row.getCell(1));

	                    // Skip the row if both wavelength and reflectance are missing
	                    if ((wavelength == null || wavelength.trim().isEmpty()) && 
	                        (reflectanceStr == null || reflectanceStr.trim().isEmpty())) {
//	                    	missingReflectance.add(wavelength);
	                        continue; // Skip this row and move to the next one
	                    }
	                    
	                    if ((wavelength == null || wavelength.trim().isEmpty()) && 
		                        (reflectanceStr != null)) {
//		                    	missingReflectance.add(wavelength);
//	                    		missingWavelengthReflectance.add("Wavelength is missing for Row " + (i));
	                    		missingWavelengthReflectance.add(String.valueOf(i)); 
		                        continue; // Skip this row and move to the next one
		                    }

	                    if ((reflectanceStr == null || reflectanceStr.trim().isEmpty())) {
//	                        throw new IllegalArgumentException("Missing reflectance value for a particular wavelength.");
	                    	missingReflectance.add(wavelength);
	                    }

	                    double reflectance = 0.0;
	                    if (reflectanceStr != null && !reflectanceStr.isEmpty()) {
	                        reflectance = Double.parseDouble(reflectanceStr);
	                        if (reflectance < 0.0 || reflectance > 1.0) {
	                            invalidReflectance.add(wavelength);
	                        }
	                    } 
//	                    else {
//	                        missingReflectance.add(wavelength);  // Handle missing reflectance case
//	                    }

//2-apr-2025	                    
//	                    // Check if the corresponding transmittance value exists in the transmittance sheet
//	                    double transmittance = 0.0;
//	                    if (containsTransmittance) {
//	                        Sheet transmittanceSheet = workbook.getSheet("transmittance");
//	                        if (transmittanceSheet != null) {
//	                            for (int j = 1; j <= transmittanceSheet.getLastRowNum(); j++) {
//	                                Row transRow = transmittanceSheet.getRow(j);
//	                                if (transRow == null) continue;
//
//	                                String transWavelength = getCellValueAsString(transRow.getCell(0));
//	                                String transmittanceStr = getCellValueAsString(transRow.getCell(1));
//
//	                                if (wavelength != null && wavelength.equals(transWavelength)) {
//	                                    if (transmittanceStr != null && !transmittanceStr.trim().isEmpty()) {
//	                                        transmittance = Double.parseDouble(transmittanceStr);
//	                                    }
//	                                    break;
//	                                }
//	                            }
//	                        }
//	                    }
//
//	                    // Validate that the sum of reflectance and transmittance doesn't exceed 1.0
//	                    if (reflectance + transmittance > 1.0) {
//	                    	errors.add("Sum of reflectance and transmittance exceeds 1.0 at wavelength " + wavelength);
//	                    }

	                    reflectanceMap.put(wavelength, reflectance);
	                    reflectanceElement.appendChild(document.createTextNode("\n" + wavelength + "\t" + reflectance + "\n"));
	                }
	            } else if ("transmittance".equalsIgnoreCase(sheet.getSheetName())) {
	                Element transmittanceElement = document.createElement("globaldata_vec2");
	                transmittanceElement.setAttribute("label", transmissivityLabel);
	                root.appendChild(transmittanceElement);

	                for (int i = 1; i <= sheet.getLastRowNum(); i++) {
	                    Row row = sheet.getRow(i);
	                    if (row == null) continue;

	                    String wavelength = getCellValueAsString(row.getCell(0));
	                    String transmittanceStr = getCellValueAsString(row.getCell(1));

	                    // Skip the row if both wavelength and transmittance are missing
	                    if ((wavelength == null || wavelength.trim().isEmpty()) && 
	                        (transmittanceStr == null || transmittanceStr.trim().isEmpty())) {
//	                    	missingTransmittance.add(wavelength);
	                        continue; // Skip this row and move to the next one
	                    }
	                    
	                    if ((wavelength == null || wavelength.trim().isEmpty()) && 
		                        (transmittanceStr != null)) {
//		                    	missingReflectance.add(wavelength);
	                    	missingWavelengthTransmittance.add(String.valueOf(i)); 
		                        continue; // Skip this row and move to the next one
		                    }


	                    if ((transmittanceStr == null || transmittanceStr.trim().isEmpty())) {
//	                        throw new IllegalArgumentException("Missing transmittance value for a particular wavelength.");
	                    	missingTransmittance.add(wavelength);
	                    }

//	                    double transmittance = Double.parseDouble(transmittanceStr);
//	                    if (transmittance < 0.0 || transmittance > 1.0) {
////	                        throw new IllegalArgumentException("Transmittance value must be between 0.0 and 1.0. for wavelength = " + wavelength);
//	                    	invalidTransmittance.add(wavelength);
//	                    }
	                    
	                    double transmittance = 0.0;
	                    if (transmittanceStr != null && !transmittanceStr.isEmpty()) {
	                    	transmittance = Double.parseDouble(transmittanceStr);
	                        if (transmittance < 0.0 || transmittance > 1.0) {
	                        	invalidTransmittance.add(wavelength);
	                        }
	                    } 

//2-apr-2025
//	                    // Check if the corresponding reflectance value exists in the reflectance sheet
//	                    double reflectance = 0.0;
//	                    if (containsReflectance) {
//	                        Sheet reflectanceSheet = workbook.getSheet("reflectance");
//	                        if (reflectanceSheet != null) {
//	                            for (int j = 1; j <= reflectanceSheet.getLastRowNum(); j++) {
//	                                Row reflectRow = reflectanceSheet.getRow(j);
//	                                if (reflectRow == null) continue;
//
//	                                String reflectWavelength = getCellValueAsString(reflectRow.getCell(0));
//	                                String reflectanceStr = getCellValueAsString(reflectRow.getCell(1));
//
//	                                if (wavelength != null && wavelength.equals(reflectWavelength)) {
//	                                    if (reflectanceStr != null && !reflectanceStr.trim().isEmpty()) {
//	                                        reflectance = Double.parseDouble(reflectanceStr);
//	                                    }
//	                                    break;
//	                                }
//	                            }
//	                        }
//	                    }
//
//	                    // Validate that the sum of reflectance and transmittance doesn't exceed 1.0
//	                    if (reflectance + transmittance > 1.0) {
//	                        throw new IllegalArgumentException("Sum of reflectance and transmittance must not exceed 1.0 at wavelength: " + wavelength);
//	                    }

	                    transmittanceMap.put(wavelength, transmittance);
	                    transmittanceElement.appendChild(document.createTextNode("\n" + wavelength + "\t" + transmittance + "\n"));
	                }
	            }
	        }
//	    }
	    
	 // Validate sum of reflectance and transmittance
        for (String wavelength : reflectanceMap.keySet()) {
            double reflectance = reflectanceMap.get(wavelength);
            double transmittance = transmittanceMap.getOrDefault(wavelength, 0.0);
            if (reflectance + transmittance > 1.0) {
            	sumExceedingWavelengths.add(wavelength);
            }
        }

        StringBuilder errorMessage = new StringBuilder("");
        if (!missingReflectance.isEmpty()) {
            errorMessage.append("- Missing reflectance for wavelengths: ").append(String.join(", ", missingReflectance)).append("\n");
        }
        if (!missingTransmittance.isEmpty()) {
            errorMessage.append("- Missing transmittance for wavelengths: ").append(String.join(", ", missingTransmittance)).append("\n");
        }
        if (!invalidReflectance.isEmpty()) {
            errorMessage.append("- Reflectance value must be between 0.0 and 1.0 for wavelengths: ").append(String.join(", ", invalidReflectance)).append("\n");
        }
        if (!invalidTransmittance.isEmpty()) {
            errorMessage.append("- Transmittance value must be between 0.0 and 1.0 for wavelengths: ").append(String.join(", ", invalidTransmittance)).append("\n");
        }
        if (!sumExceedingWavelengths.isEmpty()) {
            errorMessage.append("- Sum of reflectance and transmittance must not exceed 1.0 for wavelengths: ").append(String.join(", ", sumExceedingWavelengths)).append("\n");
        }
//        if (!missingWavelengthReflectance.isEmpty()) {
//            errorMessage.append("- Missing wavelength for reflectance at rows: ").append(String.join(", ", missingReflectance)).append("\n");
//        }
        if (!missingWavelengthReflectance.isEmpty()) {
            errorMessage.append("- Missing wavelength for reflectance at rows: ")
                        .append(String.join(", ", missingWavelengthReflectance))
                        .append("\n");
        }
        if (!missingWavelengthTransmittance.isEmpty()) {
            errorMessage.append("- Missing wavelength for transmittance at rows: ")
                        .append(String.join(", ", missingWavelengthTransmittance))
                        .append("\n");
        }
        
        if (!errorMessage.isEmpty()) {
            throw new IllegalArgumentException(String.join("\n", errorMessage));
        }

//        if (errorMessage.length() > "Validation Errors:\n".length()) {
//            throw new IllegalArgumentException(errorMessage.toString());
//        }
    }



	    TransformerFactory transformerFactory = TransformerFactory.newInstance();
	    Transformer transformer = transformerFactory.newTransformer();
	    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
	    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	    DOMSource domSource = new DOMSource(document);
	    StreamResult streamResult = new StreamResult(xmlFile);
	    transformer.transform(domSource, streamResult);

	    return xmlFile;
	}


	// Helper method to retrieve cell values as String
	private static String getCellValueAsString(Cell cell) {
		if (cell == null) {
			return "";
		}
		switch (cell.getCellType()) {
			case NUMERIC:
				return String.valueOf(cell.getNumericCellValue());
			case STRING:
				return cell.getStringCellValue();
			case BOOLEAN:
				return String.valueOf(cell.getBooleanCellValue());
			default:
				return "";
		}
	}



}
